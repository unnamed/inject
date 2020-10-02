package me.yushust.inject.key.resolve;

import me.yushust.inject.key.TypeReference;
import me.yushust.inject.key.TypeResolver;
import me.yushust.inject.key.Types;
import me.yushust.inject.util.Validate;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ParameterizedTypeResolver implements TypeResolver {

  public Type resolveType(TypeReference<?> context, Type type) {

    Validate.argument(type instanceof ParameterizedType,
        "Type isn't instance of ParameterizedType!");

    ParameterizedType parameterizedType = (ParameterizedType) type;
    Type ownerType = parameterizedType.getOwnerType();
    Type resolvedOwnerType = ContextualTypes.resolveContextually(context, ownerType);
    boolean changed = resolvedOwnerType != ownerType;

    Type[] actualTypeArgs = parameterizedType.getActualTypeArguments();

    for (int i = 0; i < actualTypeArgs.length; i++) {

      Type argument = actualTypeArgs[i];
      Type resolvedTypeArgument = ContextualTypes.resolveContextually(context, argument);

      if (resolvedTypeArgument == argument) {
        continue;
      }

      if (!changed) {
        actualTypeArgs = actualTypeArgs.clone();
        changed = true;
      }

      actualTypeArgs[i] = resolvedTypeArgument;

    }

    if (changed) {
      return Types.parameterizedTypeOf(resolvedOwnerType, parameterizedType.getRawType(), actualTypeArgs);
    }

    return parameterizedType;

  }

}
