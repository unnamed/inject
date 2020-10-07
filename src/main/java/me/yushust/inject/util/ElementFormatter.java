package me.yushust.inject.util;

import me.yushust.inject.resolve.OptionalDefinedKey;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

/**
 * Helper class for formatting elements
 * like fields, methods, annotations, etc.
 */
public final class ElementFormatter {

  private ElementFormatter() {
    throw new UnsupportedOperationException("This utility class should not be instantiated!");
  }

  public static String formatField(Field field, OptionalDefinedKey<?> key) {
    StringBuilder builder = new StringBuilder();
    if (key.isOptional()) {
      builder.append("@Nullable ");
    }
    builder.append(key.getKey().getType());
    builder.append(' ');
    builder.append(field.getName());
    return builder.toString();
  }

  public static String formatConstructor(Constructor<?> constructor, List<OptionalDefinedKey<?>> keys) {
    Validate.notNull(constructor, "constructor");
    return constructor.getDeclaringClass().getName() + '('
        + formatParameters(constructor.getParameters(), keys) + ')';
  }

  public static String formatParameters(Parameter[] parameters, List<OptionalDefinedKey<?>> keys) {

    Validate.notNull(parameters, "parameters");
    Validate.notNull(keys, "keys");
    Validate.argument(parameters.length == keys.size(), "Parameters length " +
        "and keys length must be the same");

    StringBuilder builder = new StringBuilder();

    for (int i = 0; i < parameters.length; i++) {
      Parameter parameter = parameters[i];
      OptionalDefinedKey<?> key = keys.get(i);

      if (key.isOptional()) {
        builder.append("@Nullable ");
      }
      builder.append(key.getKey().getType());
      builder.append(' ');
      builder.append(parameter.getName());

      if (i < parameters.length - 1) {
        builder.append(", ");
      }
    }

    return builder.toString();
  }

  /**
   * Formats a method to a human-friendly format like
   * <pre>MyClass#someMethod(@Nullable String, Object)</pre>
   */
  public static String formatMethod(Method method, List<OptionalDefinedKey<?>> keys) {
    return method.getDeclaringClass().getName() + '#' + method.getName() + '('
        + formatParameters(method.getParameters(), keys) + ')';
  }

}
