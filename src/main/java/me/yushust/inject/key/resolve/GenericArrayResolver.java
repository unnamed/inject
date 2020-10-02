package me.yushust.inject.key.resolve;

import me.yushust.inject.key.TypeReference;
import me.yushust.inject.key.TypeResolver;
import me.yushust.inject.key.Types;
import me.yushust.inject.util.Validate;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

public class GenericArrayResolver implements TypeResolver {

  public Type resolveType(TypeReference<?> context, Type type) {

    Validate.argument(type instanceof GenericArrayType,
        "Type isn't an instance of GenericArrayType!");

    Type componentType = ((GenericArrayType) type).getGenericComponentType();
    Type resolvedComponentType = ContextualTypes.resolveContextually(context, componentType);

    if (componentType == resolvedComponentType) {
      return type;
    }

    return Types.genericArrayTypeOf(resolvedComponentType);
  }

}
