package team.unnamed.inject.key;

import team.unnamed.inject.util.Validate;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Collection of static util methods for easy
 * Type handling.
 */
public final class Types {

    private static final Map<Class<?>, Class<?>> WRAPPER_TYPES = new HashMap<>();
    private static final List<String> OMITTED_PACKAGES = new ArrayList<>();
    private static final Type[] EMPTY_TYPE_ARRAY = new Type[]{};

    static {
        OMITTED_PACKAGES.add("java.lang.");
        OMITTED_PACKAGES.add("java.util.");

        WRAPPER_TYPES.put(int.class, Integer.class);
        WRAPPER_TYPES.put(double.class, Double.class);
        WRAPPER_TYPES.put(float.class, Float.class);
        WRAPPER_TYPES.put(short.class, Short.class);
        WRAPPER_TYPES.put(long.class, Long.class);
        WRAPPER_TYPES.put(char.class, Character.class);
        WRAPPER_TYPES.put(byte.class, Byte.class);
        WRAPPER_TYPES.put(boolean.class, Boolean.class);
    }

    private Types() {
    }

    /**
     * Adds the specified {@code packageName} to the list of omitted packages
     */
    public static void omitPackage(String packageName) {
        Validate.notEmpty(packageName, "packageName");
        OMITTED_PACKAGES.add(packageName);
    }

    private static Class<?> toWrapperIfPrimitive(Class<?> clazz) {
        Class<?> wrapper = WRAPPER_TYPES.get(clazz);
        return wrapper == null ? clazz : wrapper;
    }

    /**
     * Converts the given type to a resolvable type.
     * If the type isn't a raw type nor a TypeVariable,
     * the return type is a {@link CompositeType}.
     *
     * <p>The types are wrapped because they can variate
     * between java versions or implementations and we
     * need a consistent implementation</p>
     * <p>
     * TODO: Don't wrap just add equals method
     *
     * @param type The original type
     */
    static Type compose(Type type) {

        if (type instanceof TypeReference) {
            // unwrap, TypeReference wrapped types are already composed
            // so it's not necessary call compose(...) for the wrapped type
            return ((TypeReference<?>) type).getType();
        } else if (type instanceof Class) {
            Class<?> clazz = (Class<?>) type;
            if (clazz.isArray()) {
                // If the class is an array, we convert
                // it to a GenericArrayTypeWrapper
                return genericArrayTypeOf(clazz.getComponentType());
            }
            return toWrapperIfPrimitive(clazz);
        } else if (type instanceof CompositeType) {
            // It's already a wrapped type
            // we don't need to wrap it again
            return type;
        } else if (type instanceof ParameterizedType) {
            ParameterizedType prototype = (ParameterizedType) type;
            Type rawType = prototype.getRawType();
            // rawType is always a Class, but
            // I check because yes.
            Validate.state(rawType instanceof Class, "Raw type isn't a class!");
            return parameterizedTypeOf(
                    prototype.getOwnerType(),
                    (Class<?>) rawType,
                    prototype.getActualTypeArguments()
            );
        } else if (type instanceof GenericArrayType) {
            GenericArrayType prototype = (GenericArrayType) type;
            return genericArrayTypeOf(
                    prototype.getGenericComponentType()
            );
        } else if (type instanceof WildcardType) {
            WildcardType prototype = (WildcardType) type;
            return new WildcardTypeWrapper(
                    prototype.getUpperBounds(),
                    prototype.getLowerBounds()
            );
        }

        return type;
    }

    /**
     * Returns the raw type of the given generic (or not) type
     *
     * @param type The type.
     * @return The generic type of the type.
     */
    static Class<?> getRawType(Type type) {

        Class<?> rawType = null;

        if (type instanceof Class) {
            // it's already a raw type
            rawType = (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            // I would very happy with the
            // enhanced instanceof operator
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type typeRaw = parameterizedType.getRawType();
            // The getRawType() method should never
            // return a non-raw type, I don't know
            // why the getRawType() method returns
            // a Type and not a Class, all its implementations
            // return a Class and not a Type.
            // Why the abstraction returns Type?
            Validate.state(typeRaw instanceof Class, "Raw type isn't a Class!");
            rawType = (Class<?>) typeRaw;
        } else if (type instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType) type)
                    .getGenericComponentType();
            // Call recursively for the component type
            // (that can be generic)
            Class<?> componentRawType = getRawType(componentType);
            Object emptyArray = Array.newInstance(componentRawType, 0);
            rawType = emptyArray.getClass();
        } else if (type instanceof TypeVariable) {
            return Object.class;
        } else if (type instanceof WildcardType) {
            Type upperBound = ((WildcardType) type).getUpperBounds()[0];
            // Call recursively for the
            // upper bound
            rawType = getRawType(upperBound);
        }

        Validate.argument(rawType != null, "Cannot get raw type of '%s'", type);
        return toWrapperIfPrimitive(rawType);
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
    static String getTypeName(Type type) {
        if (type instanceof Class) {
            Class<?> clazz = (Class<?>) type;
            String className = clazz.getName();
            for (String packageName : OMITTED_PACKAGES) {
                if (className.startsWith(packageName)) {
                    className = className.substring(packageName.length());
                    break;
                }
            }
            return className;
        } else {
            return type.toString();
        }
    }

    /**
     * Static factory method to create {@link GenericArrayType}s
     */
    static GenericArrayType genericArrayTypeOf(Type type) {
        type = compose(type);
        return new GenericArrayTypeWrapper(type);
    }

    /**
     * Static factory method to create {@link ParameterizedType}s
     */
    public static ParameterizedType parameterizedTypeOf(Type ownerType, Class<?> rawType, Type... parameterTypes) {
        ownerType = compose(ownerType);
        parameterTypes = parameterTypes.clone();
        for (int i = 0; i < parameterTypes.length; i++) {
            parameterTypes[i] = compose(parameterTypes[i]);
        }
        return new ParameterizedTypeWrapper(rawType, parameterTypes, ownerType);
    }

    /**
     * Static factory method to create {@link WildcardType}s as supertype
     */
    static WildcardType wildcardSuperTypeOf(Type type) {
        type = compose(type);
        return new WildcardTypeWrapper(new Type[]{Object.class}, new Type[]{type});
    }

    /**
     * Static factory method to create {@link WildcardType} as subtype
     */
    static WildcardType wildcardSubTypeOf(Type type) {
        type = compose(type);
        return new WildcardTypeWrapper(new Type[]{type}, EMPTY_TYPE_ARRAY);
    }

    /**
     * Represents a type that's compound
     * by other types called "component types",
     * if a component type is a {@link TypeVariable}
     * or the component type is a CompositeType and
     * includes a {@link TypeVariable} as component
     * (and so on recursively), so the composite
     * type requires context (a GenericDeclaration)
     *
     * <p>In other words, if the composite type
     * includes a {@link TypeVariable} in its
     * structure, it requires context</p>
     */
    interface CompositeType {

        boolean requiresContext();

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
                = new HashSet<>();

        /**
         * Calls recursively {@link CompositeType#requiresContext()}
         * in the component types.
         *
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
                    if (((CompositeType) Types.compose(component))
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

        /**
         * Checks if the given object is equal
         * to this object. The equals implementations
         * for {@link CompositeType} may break the
         * symmetry contract of the equals method.
         *
         * <p>Why "may"? Because some implementations of
         * {@link Type} checks if the compared object
         * class is the same as the implementation</p>
         *
         * <p>This is because the types must be
         * wrapped before invoking {@link Object#equals}
         * in any type</p>
         */
        public abstract boolean equals(Object o);

    }

    /**
     * Represents a type of an array. (Arrays, dislike primitives,
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

        private GenericArrayTypeWrapper(Type componentType) {
            Validate.notNull(componentType, "componentType");
            this.componentType = componentType;
            super.components.add(this.componentType);
        }

        /**
         * The array component type
         *
         * <p>For example the type: Example[], "Example" is
         * the component type.</p>
         */
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
            // -> A null cannot be an instance of GenericArrayType! <-s
            if (!(o instanceof GenericArrayType)) return false;
            GenericArrayType that = (GenericArrayType) o;
            return componentType.equals(that.getGenericComponentType());
        }

        @Override
        public String toString() {
            return Types.getTypeName(componentType) + "[]";
        }

    }

    /**
     * Represents a parameterized type, the most known
     * generic type.
     *
     * <p>For example, a parameterized type can be List{@literal <}String{@literal >}
     * or a Map{@literal <}String, Object{@literal >}, where List
     * has 1 type arguments, and Map has 2 type arguments</p>
     */
    static class ParameterizedTypeWrapper
            extends AbstractTypeWrapper
            implements ParameterizedType {

        private final Class<?> rawType;
        private final Type[] typeArguments;
        private final Type ownerType;

        private ParameterizedTypeWrapper(
                Class<?> rawType,
                Type[] typeArguments,
                Type ownerType
        ) {
            this.rawType = rawType;
            this.ownerType = ownerType;
            this.typeArguments = typeArguments;

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
                    (ownerType == null
                            ? that.getOwnerType() == null
                            : ownerType.equals(that.getOwnerType()));
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            String clazz = rawType.getName();

            if (ownerType != null) {
                builder.append(Types.getTypeName(ownerType));
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
                    builder.append(Types.getTypeName(typeArguments[i]));
                    if (i != typeArguments.length - 1) {
                        builder.append(", ");
                    }
                }
                builder.append('>');
            }

            return builder.toString();
        }

    }

    /**
     * Represents a type with a wildcard type expression
     * like "? extends Number" ("any type that extends to Number"),
     * and "? super String" ("any type that string extends to)
     */
    static class WildcardTypeWrapper
            extends AbstractTypeWrapper
            implements WildcardType {

        private final Type[] upperBounds;
        private final Type[] lowerBounds;

        /**
         * Constructs the wildcard type expression using
         * upper bounds and lower bounds.
         *
         * <p>There only must be 1 upper bound, for an
         * unbound wildcard, use Object as upper bound</p>
         *
         * <p>There only must be zero or one lower bound</p>
         *
         * <p>Upper bounds are specified with "extends",
         * Lower bounds are specified with "super"</p>
         */
        private WildcardTypeWrapper(Type[] upperBounds, Type[] lowerBounds) {

            Validate.argument(upperBounds.length == 1,
                    "The wildcard must have 1 upper bound. For unbound wildcards, just use Object");
            Validate.argument(lowerBounds.length < 2,
                    "The wildcard must have at most 1 lower bound");

            if (lowerBounds.length == 1) {
                this.lowerBounds = new Type[]{Types.compose(lowerBounds[0])};
                this.upperBounds = new Type[]{Object.class};
            } else {
                this.lowerBounds = Types.EMPTY_TYPE_ARRAY;
                this.upperBounds = new Type[]{Types.compose(upperBounds[0])};
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
                return "? super " + Types.getTypeName(lowerBounds[0]);
            }
            if (upperBounds[0] == Object.class) {
                return "?";
            }
            return "? extends " + Types.getTypeName(upperBounds[0]);
        }

    }

}
