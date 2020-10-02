package me.yushust.inject.key.resolve;

import me.yushust.inject.key.TypeReference;
import me.yushust.inject.key.TypeResolver;
import me.yushust.inject.util.Validate;

import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

public class TypeVariableResolver implements TypeResolver {

  public Type resolveType(TypeReference<?> context, Type toResolve) {

    Validate.argument(toResolve instanceof TypeVariable,
        "Type isn't an instance of TypeVariable!");

    Type resolvedType = resolveTypeVariable(context, (TypeVariable<?>) toResolve);
    if (resolvedType == toResolve) {
      return resolvedType;
    }

    return ContextualTypes.resolveContextually(context, resolvedType);
  }

  public static Type resolveTypeVariable(TypeReference<?> cont, TypeVariable<?> resolvingType) {

    GenericDeclaration genericDeclaration = resolvingType.getGenericDeclaration();

    if (!(genericDeclaration instanceof Class)) {
      return resolvingType;
    }

    Class<?> declaration = (Class<?>) genericDeclaration;
    Type contextType = ContextualTypes.getSupertype(
        cont.getType(), cont.getRawType(), declaration);

    if (!(contextType instanceof ParameterizedType)) {
      return resolvingType;
    }

    ParameterizedType parameterizedContext = (ParameterizedType) contextType;
    TypeVariable<?>[] typeParameters = declaration.getTypeParameters();

    for (int i = 0; i < typeParameters.length; i++) {
      TypeVariable<?> typeVariable = typeParameters[i];
      if (typeVariable.equals(resolvingType)) {
        return parameterizedContext.getActualTypeArguments()[i];
      }
    }

    throw new IllegalStateException("There's no a resolvable type variable!");

  }

}
