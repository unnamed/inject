package me.yushust.inject.util;

import me.yushust.inject.key.InjectedKey;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
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

	/**
	 * Formats a method to a human-friendly format like
	 * <pre>MyClass#someMethod(@Nullable String, Object)</pre>
	 */
	public static String formatMethod(Method method, List<InjectedKey<?>> keys) {
		return method.getDeclaringClass().getName() + '#' + method.getName() + '('
				+ formatParameters(method.getParameters(), keys) + ')';
	}

	/**
	 * Converts the provided {@code annotationValues} to a string with
	 * an annotation format using the specified {@code annotationType}
	 *
	 * <p>
	 * The returned string be like:
	 * {@literal @}Annotation(value = "hello", year = 2020)
	 * {@literal @}Named("hello")
	 * {@literal @}Example(hello = "Hello", world = "World")
	 * </p>
	 */
	public static String annotationToString(Annotation annotation) {
		StringBuilder builder = new StringBuilder("@");
		builder.append(annotation.annotationType().getSimpleName());
		builder.append("(");
		Method[] methods = annotation.annotationType().getDeclaredMethods();

		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			String methodName = method.getName();
			Object value = "<non accessible>";

			try {
				value = method.invoke(annotation);
			} catch (IllegalAccessException | InvocationTargetException ignored) {
			}
			// Annotations with methodName value doesn't require
			// name specification
			if (!methodName.equals("value") || methods.length != 1) {
				builder.append(methodName);
				builder.append(" = ");
			}
			// special case that contains " at the start and end
			if (value instanceof String) {
				builder.append("\"");
				builder.append(value);
				builder.append("\"");
			} else {
				// Just append the value
				builder.append(value);
			}
			if (i != methods.length - 1) {
				builder.append(", ");
			}
		}

		builder.append(")");
		return builder.toString();
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

}
