package me.yushust.inject.error;

import me.yushust.inject.key.InjectedKey;
import me.yushust.inject.util.Validate;

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
  }

  public static String formatField(Field field, InjectedKey<?> key) {
    StringBuilder builder = new StringBuilder();
    if (key.isOptional()) {
      builder.append("@Nullable ");
    }
    builder.append(key.getKey().getType());
    builder.append(' ');
    builder.append(field.getName());
    return builder.toString();
  }

  public static String formatConstructor(Constructor<?> constructor, List<InjectedKey<?>> keys) {
    Validate.notNull(constructor, "constructor");
    return constructor.getDeclaringClass().getName() + '('
        + formatParameters(constructor.getParameters(), keys) + ')';
  }

  private static String formatParameters(Parameter[] parameters, List<InjectedKey<?>> keys) {

    Validate.notNull(parameters, "parameters");
    Validate.notNull(keys, "keys");
    Validate.argument(parameters.length == keys.size(), "Parameters length " +
        "and keys length must be the same");

    StringBuilder builder = new StringBuilder();

    for (int i = 0; i < parameters.length; i++) {
      Parameter parameter = parameters[i];
      InjectedKey<?> key = keys.get(i);

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
  public static String formatMethod(Method method, List<InjectedKey<?>> keys) {
    return method.getDeclaringClass().getName() + '#' + method.getName() + '('
        + formatParameters(method.getParameters(), keys) + ')';
  }

}
