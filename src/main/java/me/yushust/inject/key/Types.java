package me.yushust.inject.key;

import me.yushust.inject.util.Validate;

import java.lang.reflect.*;
import java.util.*;

/**
 * Collection of static util methods for easy
 * Type handling.
 */
public final class Types {

  public static final Type[] EMPTY_TYPE_ARRAY = new Type[]{};

  private Types() {
    throw new UnsupportedOperationException("This class couldn't be instantiated!");
  }

  /**
   * Converts the given type to a resolvable type.
   * If the type isn't a raw type, the return type
   * is a {@link CompositeType}.
   *
   * @param type The original type
   * @return The wrapped type
   */
  public static Type wrap(Type type) {

    Validate.notNull(type, "type");

    if (type instanceof Class) {
      Class<?> clazz = (Class<?>) type;
      if (clazz.isArray()) {
        return new GenericArrayTypeWrapper(
            wrap(clazz.getComponentType())
        );
      }
      return clazz;
    } else if (type instanceof CompositeType) {
      return type;
    } else if (type instanceof ParameterizedType) {
      ParameterizedType prototype = (ParameterizedType) type;
      return new ParameterizedTypeWrapper(prototype);
    } else if (type instanceof GenericArrayType) {
      GenericArrayType prototype = (GenericArrayType) type;
      return new GenericArrayTypeWrapper(prototype);
    } else if (type instanceof WildcardType) {
      WildcardType prototype = (WildcardType) type;
      return new WildcardTypeWrapper(prototype);
    }

    return type;
  }

  /**
   * Returns the raw type of the given generic (or not) type
   *
   * @param type The type.
   * @return The generic type of the type.
   */
  public static Class<?> getRawType(Type type) {

    Validate.notNull(type, "type");

    if (type instanceof Class) {
      return (Class<?>) type;
    } else if (type instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType) type;
      Type rawType = parameterizedType.getRawType();
      if (!(rawType instanceof Class)) {
        // wait, that's illegal
        throw new IllegalArgumentException("Raw type isn't a Class!");
      }
      return (Class<?>) rawType;
    } else if (type instanceof GenericArrayType) {
      Type componentType = ((GenericArrayType) type).getGenericComponentType();
      return Array.newInstance(getRawType(componentType), 0).getClass();
    } else if (type instanceof TypeVariable) {
      return Object.class;
    } else if (type instanceof WildcardType) {
      return getRawType(((WildcardType) type).getUpperBounds()[0]);
    }

    throw new IllegalArgumentException();
  }

  /**
   * Checks if the given types are equal.
   *
   * @param a The checked type 1
   * @param b The checked type 2
   * @return True if the given types are equal
   */
  public static boolean typeEquals(Type a, Type b) {

    if (a == b) {
      return true;
    } else if (a instanceof Class) {
      return a.equals(b);
    } else if (a instanceof ParameterizedType) {

      if (!(b instanceof ParameterizedType)) {
        return false;
      }

      ParameterizedType pa = (ParameterizedType) a;
      ParameterizedType pb = (ParameterizedType) b;

      Type aOwnerType = pa.getOwnerType();
      Type bOwnerType = pb.getOwnerType();

      return aOwnerType == null ? bOwnerType == null : aOwnerType.equals(bOwnerType)
          && pa.getRawType().equals(pb.getRawType())
          && Arrays.equals(pa.getActualTypeArguments(), pb.getActualTypeArguments());

    } else if (a instanceof GenericArrayType) {
      if (!(b instanceof GenericArrayType)) {
        return false;
      }

      GenericArrayType ga = (GenericArrayType) a;
      GenericArrayType gb = (GenericArrayType) b;
      return typeEquals(ga.getGenericComponentType(), gb.getGenericComponentType());
    } else if (a instanceof WildcardType) {
      if (!(b instanceof WildcardType)) {
        return false;
      }

      WildcardType wa = (WildcardType) a;
      WildcardType wb = (WildcardType) b;
      return Arrays.equals(wa.getUpperBounds(), wb.getUpperBounds())
          && Arrays.equals(wa.getLowerBounds(), wb.getLowerBounds());

    } else if (a instanceof TypeVariable) {

      if (!(b instanceof TypeVariable)) {
        return false;
      }

      TypeVariable<?> va = (TypeVariable<?>) a;
      TypeVariable<?> vb = (TypeVariable<?>) b;

      return va.getGenericDeclaration() == vb.getGenericDeclaration()
          && va.getName().equals(vb.getName());
    }

    return false;
  }

  /**
   * Converts the given type of the string, if the given type
   * is an string, returns the class name. If the given type
   * is a generic type, uses {@link Type#toString} to get the
   * type as a string.
   *
   * @param type The type
   * @return The type converted to string
   */
  public static String asString(Type type) {
    return type instanceof Class ? ((Class<?>) type).getName() : type.toString();
  }

  public static GenericArrayType genericArrayTypeOf(Type type) {
    return new GenericArrayTypeWrapper(type);
  }

  public static ParameterizedType parameterizedTypeOf(Type ownerType, Class<?> rawType, Type... parameterTypes) {
    return new ParameterizedTypeWrapper(rawType, parameterTypes, ownerType);
  }

  public static WildcardType wildcardSuperTypeOf(Type type) {
    return new WildcardTypeWrapper(new Type[]{Object.class}, new Type[]{type});
  }

  public static WildcardType wildcardSubTypeOf(Type type) {
    return new WildcardTypeWrapper(new Type[]{type}, EMPTY_TYPE_ARRAY);
  }

  /**
   * An abstract class that adds some methods for
   * comparing types and implementing
   * {@link CompositeType#requiresContext()} method
   * by checking all the types that compose this composite
   * type.
   */
  static abstract class AbstractTypeWrapper implements Type, CompositeType {

    // the map must be modified only on
    // the initialization of a composite type
    protected final Set<Type> components
        = new HashSet<Type>();

    /**
     * Calls recursively {@link CompositeType#requiresContext()}
     * in the component types.
     * @return True if the type isn't fully-specified
     */
    public boolean requiresContext() {
      for (Type component : components) {
        if (component instanceof CompositeType) {
          // the component is a CompositeType, we
          // can just call CompositeType#requiresContext
          if (((CompositeType) component)
              .requiresContext()) {
            return true;
          }
        } else if (component instanceof TypeVariable) {
          // it's type variable, it requires context,
          // this is the base case
          return true;
        } else if (!(component instanceof Class)) {
          // If the type isn't a class, nor
          // a CompositeType, nor a TypeVariable,
          // it should be wrapped and the return
          // type is always a CompositeType
          if (((CompositeType) Types.wrap(component))
              .requiresContext()) {
            return true;
          }
        }
      }
      return false;
    }

    // The equals method must be specified
    // by the sub-class

    @Override
    public int hashCode() {
      int result = 1;
      for (Type component : components) {
        result = 31 * result + component.hashCode();
      }
      return result;
    }
  }

  /**
   * Represents a type of an array. (Arrays, dislike primitves,
   * are real objects, we can get the type of a raw
   * or generic array).
   *
   * <p>Like String[], Object[], int[], Class{@literal <}?{@literal >}[],
   * etc.</p>
   */
  static class GenericArrayTypeWrapper
      extends AbstractTypeWrapper
      implements GenericArrayType {

    private final Type componentType;

    GenericArrayTypeWrapper(Type componentType) {
      this.componentType = Validate.notNull(componentType, "componentType");
      super.components.add(componentType);
    }

    public Type getGenericComponentType() {
      return componentType;
    }

    // The hashCode() method is already
    // specified by AbstractTypeWrapper,
    // we just need to hash the componentType,
    // already added as a component for
    // the AbstractTypeWrapper.

    @Override
    public boolean equals(Object o) {
      // identity, it's the same object
      if (this == o) return true;
      // The instanceof operator already checks nullability
      // -> A null cannot be an instance of GenericArrayType! <-
      // this can maybe break the symmetry contract of
      // the equals method with this check, so to compare
      // two wrapped types, you must first wrap both types
      if (!(o instanceof GenericArrayType)) return false;
      GenericArrayType that = (GenericArrayType) o;
      return componentType.equals(that.getGenericComponentType());
    }

    @Override
    public String toString() {
      return Types.asString(componentType) + "[]";
    }
  }

  static class ParameterizedTypeWrapper
      extends AbstractTypeWrapper
      implements ParameterizedType {

    private final Class<?> rawType;
    private final Type[] typeArguments;
    private final Type ownerType;

    ParameterizedTypeWrapper(
        Class<?> rawType,
        Type[] typeArguments,
        Type ownerType
    ) {
      this.rawType = rawType;
      this.ownerType = ownerType == null ? null : Types.wrap(ownerType);
      this.typeArguments = typeArguments.clone();

      for (int t = 0, length = this.typeArguments.length; t < length; t++) {
        this.typeArguments[t] = Types.wrap(this.typeArguments[t]);
      }

      Collections.addAll(super.components, typeArguments);
      if (ownerType != null) {
        super.components.add(ownerType);
      }
    }

    public Type getRawType() {
      return rawType;
    }

    public Type[] getActualTypeArguments() {
      return typeArguments;
    }

    public Type getOwnerType() {
      return ownerType;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof ParameterizedType)) return false;
      ParameterizedType that = (ParameterizedType) o;
      return rawType.equals(that.getRawType()) &&
          Arrays.equals(typeArguments, that.getActualTypeArguments()) &&
          ownerType == null
            ? that.getOwnerType() == null
            : ownerType.equals(that.getOwnerType());
    }

    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder();
      String clazz = rawType.getName();

      if (ownerType != null) {
        builder.append(Types.asString(ownerType));
        builder.append('.');

        String prefix = ownerType instanceof ParameterizedType
            ? ((Class<?>) ((ParameterizedType) ownerType).getRawType()).getName() + '$'
            : ((Class<?>) ownerType).getName() + '$';

        if (clazz.startsWith(prefix)) {
          clazz = clazz.substring(prefix.length());
        }
      }

      builder.append(clazz);

      if (typeArguments.length != 0) {
        builder.append('<');
        for (int i = 0; i < typeArguments.length; i++) {
          builder.append(Types.asString(typeArguments[i]));
          if (i != typeArguments.length - 1) {
            builder.append(", ");
          }
        }
        builder.append('>');
      }

      return builder.toString();
    }
  }

  static class WildcardTypeWrapper
      extends AbstractTypeWrapper
      implements WildcardType {

    private final Type[] upperBounds;
    private final Type[] lowerBounds;

    WildcardTypeWrapper(Type[] upperBounds, Type[] lowerBounds) {

      Validate.argument(upperBounds.length == 1,
          "The wildcard must have 1 upper bound. For unbound wildcards, just use Object");
      Validate.argument(lowerBounds.length < 2,
          "The wildcard must have at most 1 lower bound");

      if (lowerBounds.length == 1) {
        this.lowerBounds = new Type[]{Types.wrap(lowerBounds[0])};
        this.upperBounds = new Type[]{Object.class};
      } else {
        this.lowerBounds = Types.EMPTY_TYPE_ARRAY;
        this.upperBounds = new Type[]{Types.wrap(upperBounds[0])};
      }

      Collections.addAll(super.components, this.upperBounds);
      Collections.addAll(super.components, this.lowerBounds);
    }

    public Type[] getUpperBounds() {
      return upperBounds;
    }

    public Type[] getLowerBounds() {
      return lowerBounds;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof WildcardType)) return false;
      WildcardType other = (WildcardType) o;
      return Arrays.equals(upperBounds, other.getUpperBounds())
          && Arrays.equals(lowerBounds, other.getLowerBounds());
    }

    @Override
    public String toString() {
      if (lowerBounds.length == 1) {
        return "? super " + Types.asString(lowerBounds[0]);
      }
      if (upperBounds[0] == Object.class) {
        return "?";
      }
      return "? extends " + Types.asString(upperBounds[0]);
    }

  }


}