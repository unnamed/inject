package me.yushust.inject.key;

import me.yushust.inject.util.Validate;

import java.lang.reflect.*;
import java.util.Arrays;

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
        return new GenericArrayTypeReference(
            wrap(clazz.getComponentType())
        );
      }
      return clazz;
    } else if (type instanceof CompositeType) {
      return type;
    } else if (type instanceof ParameterizedType) {
      ParameterizedType prototype = (ParameterizedType) type;
      return new ParameterizedTypeReference(prototype);
    } else if (type instanceof GenericArrayType) {
      GenericArrayType prototype = (GenericArrayType) type;
      return new GenericArrayTypeReference(prototype);
    } else if (type instanceof WildcardType) {
      WildcardType prototype = (WildcardType) type;
      return new WildcardTypeReference(prototype);
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
    return new GenericArrayTypeReference(type);
  }

  public static ParameterizedType parameterizedTypeOf(Type ownerType, Type rawType, Type... parameterTypes) {
    return new ParameterizedTypeReference(ownerType, rawType, parameterTypes);
  }

  public static WildcardType wildcardSuperTypeOf(Type type) {
    return new WildcardTypeReference(new Type[]{Object.class}, new Type[]{type});
  }

  public static WildcardType wildcardSubTypeOf(Type type) {
    return new WildcardTypeReference(new Type[]{type}, EMPTY_TYPE_ARRAY);
  }

}