package me.yushust.inject.resolve;

import me.yushust.inject.key.Key;
import me.yushust.inject.util.Validate;

import java.util.Objects;

/**
 * An extension for {@link Key} (using composition over inheritance)
 * that adds two boolean states representing the requirement of the
 * injection of this key and if this key will be assisted or not.
 */
public final class OptionalDefinedKey<T> {

  private final Key<T> key;
  private final boolean optional;
  private final boolean assisted;

  public OptionalDefinedKey(Key<T> key, boolean optional, boolean assisted) {
    this.key = Validate.notNull(key, "key");
    this.optional = optional;
    this.assisted = assisted;
  }

  public Key<T> getKey() {
    return key;
  }

  public boolean isOptional() {
    return optional;
  }

  public boolean isAssisted() {
    return assisted;
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
        && assisted == that.assisted
        && key.equals(that.key);
  }

  @Override
  public int hashCode() {
    return Objects.hash(optional, assisted, key);
  }
}
