package me.yushust.inject.error;

import me.yushust.inject.Injector;

import java.util.List;

/**
 * Represents an object that can contain a collection of errors
 * and then, can report all attached errors.
 *
 * <p>The recollection of all generated errors while configuring
 * an {@link Injector}, instantiating a class, injecting fields or
 * methods is very important. We need to report all the errors,
 * not only the first (Uncaught exceptions breaks the execution)</p>
 */
public interface ErrorAttachable {

  /**
   * Stores all the {@code errorMessages} in this object
   *
   * @param errorMessages The reported error messages
   */
  void attach(String... errorMessages);

  /**
   * Attaches the specified error using the specified
   * reason/title/header.
   */
  void attach(String header, Throwable error);

  /**
   * Stores all the errors of the specified {@code attachable}
   * into this object
   *
   * @param attachable The error attachable
   */
  void attachAll(ErrorAttachable attachable);

  /**
   * Gets an immutable copy of all the attached
   * error messages.
   *
   * @return The attached error messages
   */
  List<String> getErrorMessages();

  /**
   * Applies a snapshot, memento design pattern
   */
  void applySnapshot(List<String> errorMessages);

  /**
   * Formats the error messages in one message
   */
  String formatMessages();

  /** Returns the current error count */
  int errorCount();

  /**
   * @return True if the errors has been attached
   * to this object.
   */
  boolean hasErrors();

  /**
   * Removes all the attached errors and reports it
   * to the user
   */
  void reportAttachedErrors();

}
