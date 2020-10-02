package me.yushust.inject.resolve;

import me.yushust.inject.key.Key;
import me.yushust.inject.util.Validate;

/**
 * An extension for {@link Key} (using composition over inheritance)
 * that adds a boolean state representing the requirement of the
 * injection of this key.
 */
public final class OptionalDefinedKey<T> {

  private final Key<T> key;
  private final boolean optional;

  public OptionalDefinedKey(Key<T> key, boolean optional) {
    this.key = Validate.notNull(key, "key");
    this.optional = optional;
  }

  public Key<T> getKey() {
    return key;
  }

  public boolean isOptional() {
    return optional;
  }

  @Override
  public String toString() {
    return (optional ? "(optional) " : "(required) ")
        + key.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    OptionalDefinedKey<?> that = (OptionalDefinedKey<?>) o;
    return optional == that.optional
        && key.equals(that.key);
  }

  @Override
  public int hashCode() {
    int result = 1;
    result = 31 * result + (optional ? 1 : 0);
    result = 31 * result + key.hashCode();
    return result;
  }
}
