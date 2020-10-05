package me.yushust.inject.error;

/**
 * Represents an object prone to have errors.
 *
 * <p>This ErrorAttachable implementation doesn't
 * permit exception reporting. You must report
 * the errors by manually throwing an exception
 * passing {@link ErrorAttachable#formatMessages}
 * as message/p>
 *
 * @param <T> The object type
 */
public class ErrorProne<T> extends ErrorAttachableImpl {

  // The prone value, it can be null
  private final T value;

  public ErrorProne(T value) {
    this.value = value;
  }

  /** Returns the value prone to errors*/
  public T getValue() {
    return value;
  }
}
