package me.yushust.inject.key;

import me.yushust.inject.key.resolve.ContextualTypes;
import me.yushust.inject.util.Validate;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

/**
 * GenericArrayType represents an array type
 * whose component type is either a parameterized type or a type variable.
 */
class GenericArrayTypeReference implements GenericArrayType, CompositeType {

  private final Type componentType;

  GenericArrayTypeReference(GenericArrayType prototype) {
    this(prototype.getGenericComponentType());
  }

  GenericArrayTypeReference(Type componentType) {
    Validate.notNull(componentType);
    this.componentType = Types.wrap(componentType);
  }

  public Type getGenericComponentType() {
    return this.componentType;
  }

  public boolean requiresContext() {
    return ContextualTypes.requiresContext(componentType);
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof GenericArrayType)) {
      return false;
    }
    GenericArrayType other = (GenericArrayType) o;
    return Types.typeEquals(this, other);
  }

  @Override
  public int hashCode() {
    return componentType.hashCode();
  }

  @Override
  public String toString() {
    return Types.asString(componentType) + "[]";
  }

}
