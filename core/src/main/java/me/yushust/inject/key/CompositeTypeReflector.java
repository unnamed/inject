package me.yushust.inject.key;

import java.lang.reflect.*;

/**
 * Collection of util methods for contextually
 * handling of types.
 */
final class CompositeTypeReflector {

  private CompositeTypeReflector() {
  }

  private static Type resolveTypeVariable(
      TypeReference<?> context,
      TypeVariable<?> typeVariable
  ) {
    GenericDeclaration declaration = typeVariable.getGenericDeclaration();

    // we can just resolve type variables
    // declared in classes, not in methods or
    // constructors
    if (!(declaration instanceof Class)) {
      return typeVariable;
    }

    Class<?> classDeclaration = (Class<?>) declaration;
    TypeVariable<?>[] parameters = classDeclaration.getTypeParameters();

    Type contextType = context.getType();
    Class<?> contextRawType = context.getRawType();

    Type contextSupertype;

    // resolve the super-type
    fallback: while (true) {
      if (classDeclaration == contextRawType) {
        contextSupertype = contextType;
        break;
      } else if (classDeclaration.isInterface()) {
        Class<?>[] rawInterfaceTypes = contextRawType.getInterfaces();
        Type[] genericInterfaceTypes = contextRawType.getGenericInterfaces();

        for (int i = 0; i < rawInterfaceTypes.length; i++) {
          Class<?> rawInterfaceType = rawInterfaceTypes[i];
          Type interfaceType = genericInterfaceTypes[i];
          if (rawInterfaceType == classDeclaration) {
            contextSupertype = interfaceType;
            break fallback;
          } else if (classDeclaration.isAssignableFrom(rawInterfaceType)) {
            contextType = interfaceType;
            contextRawType = rawInterfaceType;
            continue fallback;
          }
        }
      }

      if (contextRawType.isInterface() || contextRawType == Object.class) {
        contextSupertype = classDeclaration;
        break;
      }

      for (
          Class<?> rawSupertype = contextRawType.getSuperclass();
          contextRawType != null && contextRawType != Object.class;
          contextRawType = (rawSupertype = contextRawType.getSuperclass())
      ) {
        if (rawSupertype == classDeclaration) {
          return contextRawType.getGenericSuperclass();
        } else if (classDeclaration.isAssignableFrom(rawSupertype)) {
          contextType = contextRawType.getGenericSuperclass();
          contextRawType = rawSupertype;
          continue fallback;
        }
      }

      contextSupertype = classDeclaration;
      break;
    }

    // it doesn't require a resolution
    if (!(contextSupertype instanceof ParameterizedType)) {
      return typeVariable;
    }

    for (int i = 0; i < parameters.length; i++) {
      TypeVariable<?> parameter = parameters[i];
      // we found the parameter that
      // must be resolved
      if (parameter.equals(typeVariable)) {
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

  private static Type resolveWildcardType(
      TypeReference<?> context,
      WildcardType wildcardType
  ) {

    Type[] lowerBounds = wildcardType.getLowerBounds();
    Type[] upperBounds = wildcardType.getUpperBounds();

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

    return wildcardType;
  }

  private static Type resolveParameterizedType(
      TypeReference<?> context,
      ParameterizedType type
  ) {

    Type ownerType = type.getOwnerType();
    Type resolvedOwnerType = resolveContextually(context, ownerType);

    Type[] typeParameters = type.getActualTypeArguments();
    boolean changed;

    if (changed = resolvedOwnerType != ownerType) {
      typeParameters = typeParameters.clone();
    }

    for (int i = 0; i < typeParameters.length; i++) {

      Type typeParameter = typeParameters[i];
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

    if (changed) {
      // Here the type has changed,
      // so we create a new ParameterizedType
      Type rawType = type.getRawType();
      return Types.parameterizedTypeOf(
          resolvedOwnerType,
          (Class<?>) rawType,
          typeParameters
      );
    } else {
      // Nothing changed, return the same type
      return type;
    }
  }

  private static Type resolveGenericArrayType(TypeReference<?> context, GenericArrayType genericArrayType) {

    Type componentType = genericArrayType.getGenericComponentType();
    Type resolvedComponentType = resolveContextually(context, componentType);

    if (componentType == resolvedComponentType) {
      // no changes, return the same type
      return genericArrayType;
    } else {
      // create a new updated generic array type
      // with the resolved type
      return Types.genericArrayTypeOf(resolvedComponentType);
    }
  }

  /**
   * Resolves the type contextually.
   *
   * @param context The context
   * @param type    The possibly non-fully-specified type
   * @return A fully specified type
   */
  static Type resolveContextually(TypeReference<?> context, Type type) {
    if (type instanceof TypeVariable) {
      return resolveTypeVariable(context, (TypeVariable<?>) type);
    } else if (type instanceof WildcardType) {
      return resolveWildcardType(context, (WildcardType) type);
    } else if (type instanceof ParameterizedType) {
      return resolveParameterizedType(context, (ParameterizedType) type);
    } else if (type instanceof GenericArrayType) {
      return resolveGenericArrayType(context, (GenericArrayType) type);
    } else {
      return type;
    }
  }

}
