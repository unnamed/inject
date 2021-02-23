package me.yushust.inject.key;

import me.yushust.inject.util.Validate;

import java.lang.reflect.*;
import java.util.*;

/**
 * Collection of util methods for contextually
 * handling of types.
 */
final class CompositeTypeReflector {

  private static final List<TypeReflector<?>> REFLECTORS =
      new LinkedList<>();

  static {
    REFLECTORS.add(new TypeVariableReflector());
    REFLECTORS.add(new GenericArrayReflector());
    REFLECTORS.add(new ParameterizedTypeReflector());
    REFLECTORS.add(new WildcardTypeReflector());
  }

  private CompositeTypeReflector() {
  }

  private static Type getSupertype(Type type, Class<?> rawType, Class<?> resolvingType) {

    Validate.notNull(type, "type");
    Validate.notNull(rawType, "rawType");
    Validate.notNull(resolvingType, "resolvingType");

    if (resolvingType == rawType) {
      return type;
    }

    if (resolvingType.isInterface()) {
      Class<?>[] rawInterfaceTypes = rawType.getInterfaces();
      Type[] genericInterfaceTypes = rawType.getGenericInterfaces();

      for (int i = 0; i < rawInterfaceTypes.length; i++) {
        Class<?> rawInterfaceType = rawInterfaceTypes[i];
        Type interfaceType = genericInterfaceTypes[i];
        if (rawInterfaceType == resolvingType) {
          return interfaceType;
        } else if (resolvingType.isAssignableFrom(rawInterfaceType)) {
          return getSupertype(interfaceType, rawInterfaceType, resolvingType);
        }
      }
    }

    if (rawType.isInterface() || rawType == Object.class) {
      return resolvingType;
    }

    for (
        Class<?> rawSupertype = rawType.getSuperclass();
        rawType != null && rawType != Object.class;
        rawType = (rawSupertype = rawType.getSuperclass())
    ) {
      if (rawSupertype == resolvingType) {
        return rawType.getGenericSuperclass();
      } else if (resolvingType.isAssignableFrom(rawSupertype)) {
        return getSupertype(rawType.getGenericSuperclass(), rawSupertype, resolvingType);
      }
    }

    return resolvingType;

  }

  /**
   * Resolves the type contextually.
   *
   * @param context The context
   * @param type    The possibly non-fully-specified type
   * @return A fully specified type
   */
  static Type resolveContextually(TypeReference<?> context, Type type) {

    Validate.notNull(context);

    for (@SuppressWarnings("rawtypes")
        TypeReflector reflector : REFLECTORS) {

      if (reflector.getExpectedType().isInstance(type)) {
        @SuppressWarnings("unchecked")
        Type resolved = reflector.resolveType(context, type);
        return resolved;
      }
    }

    return type;
  }

  /**
   * Is a functional interface that resolves a
   * type that requires a context to be a
   * fully-specified type.
   */
  interface TypeReflector<T extends Type> {

    Class<T> getExpectedType();

    Type resolveType(TypeReference<?> context, T type);

  }

  static class TypeVariableReflector implements TypeReflector<TypeVariable<?>> {

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Class<TypeVariable<?>> getExpectedType() {
      return (Class<TypeVariable<?>>) (Class) TypeVariable.class;
    }

    public Type resolveType(TypeReference<?> context, TypeVariable<?> type) {

      GenericDeclaration declaration = type.getGenericDeclaration();

      // we can just resolve type variables
      // declared in classes, not in methods or
      // constructors
      if (!(declaration instanceof Class)) {
        return type;
      }

      Class<?> classDeclaration = (Class<?>) declaration;
      TypeVariable<?>[] parameters = classDeclaration.getTypeParameters();

      Type contextSupertype = getSupertype(
          context.getType(), context.getRawType(), classDeclaration
      );

      // it doesn't require a resolution
      if (!(contextSupertype instanceof ParameterizedType)) {
        return type;
      }

      for (int i = 0; i < parameters.length; i++) {
        TypeVariable<?> parameter = parameters[i];
        // we found the parameter that
        // must be resolved
        if (parameter.equals(type)) {
          // resolve the parameter using
          // the same context
          return resolveContextually(
              context,
              ((ParameterizedType) contextSupertype)
                  .getActualTypeArguments()[i]
          );
        }
      }

      throw new IllegalStateException("Cannot resolve type variable, no type argument found");
    }

  }

  static class WildcardTypeReflector implements TypeReflector<WildcardType> {

    public Class<WildcardType> getExpectedType() {
      return WildcardType.class;
    }

    public Type resolveType(TypeReference<?> context, WildcardType type) {

      Type[] lowerBounds = type.getLowerBounds();
      Type[] upperBounds = type.getUpperBounds();

      if (lowerBounds.length == 1) {
        // we resolve the lower bound here
        // using the same context
        Type lowerBound = lowerBounds[0];
        Type resolvedLowerBound = resolveContextually(context, lowerBound);
        if (lowerBound != resolvedLowerBound) {
          // the type changed, so we create a new
          // wildcard type using the new lower bound
          return Types.wildcardSuperTypeOf(resolvedLowerBound);
        }
      }

      if (upperBounds.length == 1) {
        // we resolve the upper bound here
        // using the same context
        Type upperBound = upperBounds[0];
        Type resolvedUpperBound = resolveContextually(context, upperBound);
        if (upperBound != resolvedUpperBound) {
          // the type changed, so we create a new
          // wildcard type using the new upper bound
          return Types.wildcardSubTypeOf(resolvedUpperBound);
        }
      }

      return type;
    }

  }

  public static class ParameterizedTypeReflector implements TypeReflector<ParameterizedType> {

    public Class<ParameterizedType> getExpectedType() {
      return ParameterizedType.class;
    }

    public Type resolveType(TypeReference<?> context, ParameterizedType type) {

      Type ownerType = type.getOwnerType();
      Type resolvedOwnerType = resolveContextually(context, ownerType);
      boolean changed = resolvedOwnerType != ownerType;

      Type[] typeParameters = type.getActualTypeArguments();

      for (int i = 0; i < typeParameters.length; i++) {
        Type typeParameter = typeParameters[i];
        // Resolve the type parameter recursively
        Type resolvedTypeParameter = resolveContextually(context, typeParameter);

        // The type changed, it can be resolved now
        if (typeParameter != resolvedTypeParameter) {
          if (!changed) {
            // the array of type parameters is cloned
            // only if a type parameter changes
            typeParameters = typeParameters.clone();
            changed = true;
          }

          // replace the type parameter
          typeParameters[i] = resolvedTypeParameter;
        }
      }

      if (!changed) {
        // Nothing changed, return the same type
        return type;
      }

      // Here the type has changed,
      // so we create a new ParameterizedType
      Type rawType = type.getRawType();
      Validate.state(rawType instanceof Class, "Raw type isn't a class!");
      return Types.parameterizedTypeOf(
          resolvedOwnerType,
          (Class<?>) rawType,
          typeParameters
      );
    }

  }

  public static class GenericArrayReflector implements TypeReflector<GenericArrayType> {

    public Class<GenericArrayType> getExpectedType() {
      return GenericArrayType.class;
    }

    public Type resolveType(TypeReference<?> context, GenericArrayType type) {
      // The GenericArrayReflector only
      // calls the resolveContextually(...)
      // method passing the array component
      // type as the type to be resolved
      Type componentType = type.getGenericComponentType();
      Type resolvedComponentType = resolveContextually(context, componentType);

      // Nothing changed in resolution,
      // return the same object, there's
      // no necessity to create a new type
      // with the same component type
      if (componentType == resolvedComponentType) {
        return type;
      }

      // the component type changed, we need to
      // create another GenericArrayType because
      // the given type is immutable
      return Types.genericArrayTypeOf(resolvedComponentType);
    }

  }
}
