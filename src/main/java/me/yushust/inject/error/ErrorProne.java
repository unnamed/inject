package me.yushust.inject.error;

/**
 * Represents an object prone to have errors
 * @param <T> The object type
 */
public class ErrorProne<T> extends ErrorAttachableImpl {

  // The prone value, it can be null
  private final T value;

  public ErrorProne(T value) {
    this.value = value;
  }

  public void reportAttachedErrors() {
    throw new UnsupportedOperationException("Cannot report attached errors in a ErrorProne");
  }

  public T getValue() {
    return value;
  }
}
