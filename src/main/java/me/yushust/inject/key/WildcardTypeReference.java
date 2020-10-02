package me.yushust.inject.key;

import me.yushust.inject.key.resolve.ContextualTypes;
import me.yushust.inject.util.Validate;

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;

/**
 * Represents a wildcard type expression, such
 * as ?, ? extends Number, or ? super Integer.
 */
class WildcardTypeReference implements WildcardType, CompositeType {

  private final Type[] upperBounds;
  private final Type[] lowerBounds;

  WildcardTypeReference(WildcardType prototype) {
    this(prototype.getUpperBounds(), prototype.getLowerBounds());
  }

  WildcardTypeReference(Type[] upperBounds, Type[] lowerBounds) {

    Validate.argument(upperBounds.length == 1,
        "The wildcard must have 1 upper bound");
    Validate.argument(lowerBounds.length < 2,
        "The wildcard must have at most 1 lower bound");

    if (lowerBounds.length == 1) {
      this.lowerBounds = new Type[]{Types.wrap(lowerBounds[0])};
      this.upperBounds = new Type[]{Object.class};
    } else {
      this.lowerBounds = Types.EMPTY_TYPE_ARRAY;
      this.upperBounds = new Type[]{Types.wrap(upperBounds[0])};
    }
  }

  public Type[] getUpperBounds() {
    return this.upperBounds;
  }

  public Type[] getLowerBounds() {
    return this.lowerBounds;
  }

  public boolean requiresContext() {
    return ContextualTypes.requiresContext(upperBounds[0])
        || (lowerBounds.length == 1 && ContextualTypes.requiresContext(lowerBounds[0]));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof WildcardType)) {
      return false;
    }
    WildcardType other = (WildcardType) o;
    return Types.typeEquals(this, other);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(lowerBounds) ^ Arrays.hashCode(upperBounds);
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
