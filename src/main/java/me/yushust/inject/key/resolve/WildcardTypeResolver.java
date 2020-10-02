package me.yushust.inject.key.resolve;

import me.yushust.inject.key.TypeReference;
import me.yushust.inject.key.TypeResolver;
import me.yushust.inject.key.Types;
import me.yushust.inject.util.Validate;

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

public class WildcardTypeResolver implements TypeResolver {

  public Type resolveType(TypeReference<?> context, Type type) {

    Validate.argument(type instanceof WildcardType, "Type isn't instanceof WildcardType!");

    WildcardType wildcard = (WildcardType) type;
    Type[] lowerBounds = wildcard.getLowerBounds();
    Type[] upperBounds = wildcard.getUpperBounds();

    if (lowerBounds.length == 1) {
      Type lowerBound = lowerBounds[0];
      Type resolvedLowerBound = ContextualTypes.resolveContextually(context, lowerBound);
      if (lowerBound == resolvedLowerBound) {
        return type;
      }
      return Types.wildcardSuperTypeOf(resolvedLowerBound);
    }

    if (upperBounds.length == 1) {
      Type upperBound = upperBounds[0];
      Type resolvedUpperBound = ContextualTypes.resolveContextually(context, upperBound);
      if (resolvedUpperBound != upperBound) {
        return Types.wildcardSubTypeOf(resolvedUpperBound);
      }
    }

    return type;
  }

}
