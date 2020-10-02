package me.yushust.inject.error;

import me.yushust.inject.util.Validate;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

/**
 * Collection of static util methods for
 * ease the handling of {@link Throwable}s
 * and error messages.
 */
public final class Errors {

  private Errors() {
    throw new UnsupportedOperationException("Don't instantiate this class!");
  }

  /**
   * Prints the stack trace of the specified {@code throwable}
   * to a {@link StringWriter} and returns the printed stack
   * trace.
   *
   * @param throwable The throwable
   * @return The throwable stack trace
   */
  public static String getStackTrace(Throwable throwable) {
    Validate.notNull(throwable);
    // The StringWriter doesn't require a flush() or close()
    StringWriter writer = new StringWriter();
    // The PrintWriter just flushes the delegated writer
    throwable.printStackTrace(new PrintWriter(writer));
    return writer.toString();
  }

  public static String formatErrorMessages(List<String> messages) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < messages.size(); i++) {
    }
    return builder.toString();
  }

}
