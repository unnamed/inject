package me.yushust.inject;

import me.yushust.inject.key.Qualifier;
import me.yushust.inject.resolve.QualifierFactory;
import me.yushust.inject.util.Validate;

import javax.inject.Named;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Collection of factory static methods and other util
 * methods for ease the handling of qualifiers
 */
public final class Qualifiers {

  private Qualifiers() {
    // Don't try to construct an util class!
    throw new UnsupportedOperationException("This class couldn't be instantiated!");
  }

  public static boolean containsOnlyDefaultValues(Annotation annotation) {

    for (Method method : annotation.annotationType().getDeclaredMethods()) {

      Object defaultValue = method.getDefaultValue();
      if (defaultValue == null) {
        return false;
      }

      Object value;
      try {
        value = method.invoke(annotation);
      } catch (IllegalAccessException | InvocationTargetException ignored) {
        continue;
      }

      if (!defaultValue.equals(value)) {
        return false;
      }
    }

    return true;
  }

  /**
   * Converts the provided {@code annotationValues} to a string with
   * an annotation format using the specified {@code annotationType}
   *
   * <p>
   * The returned string be like:
   *   {@literal @}Annotation(value = "hello", year = 2020)
   *   {@literal @}Named("hello")
   *   {@literal @}Example(hello = "Hello", world = "World")
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

  /**
   * Checks for annotations annotated with {@link javax.inject.Qualifier} and passes
   * the valid qualifiers to the qualifier factory, to convert the
   * annotations to a real {@link Qualifier}
   * @return The collection of valid found qualifiers
   */
  public static Set<Qualifier> getQualifiers(QualifierFactory factory, Annotation[] annotations) {
    Set<Qualifier> qualifiers = new HashSet<>();
    for (Annotation annotation : annotations) {
      if (!annotation.annotationType().isAnnotationPresent(javax.inject.Qualifier.class)) {
        continue;
      }
      Qualifier qualifier = factory.getQualifier(annotation);
      if (qualifier == null) {
        continue;
      }
      qualifiers.add(qualifier);
    }
    return qualifiers;
  }

  /**
   * Creates an instance of {@link Named} with
   * the specified {@code name} as value for
   * this annotation.
   *
   * @param name The name for the annotation
   * @return The instance of the annotation using
   * the specified {@code name}
   */
  public static Named createNamed(String name) {
    // not Validate.notEmpty(name), the name can be an empty string
    Validate.notNull(name);
    return new NamedImpl(name);
  }

  private static class NamedImpl implements Named {

    private final String name;
    // the object will never change,
    // the hashCode can be cached
    private final int hashCode;
    // same for the toString() method
    private final String toString;

    private NamedImpl(String name) {
      this.name = name;
      this.hashCode = (127 * "value".hashCode()) ^ name.hashCode();
      this.toString = "@Named(\"" + name + "\")";
    }

    public Class<? extends Annotation> annotationType() {
      return Named.class;
    }

    public String value() {
      return name;
    }

    @Override
    public int hashCode() {
      return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (!(obj instanceof Named)) return false;
      return name.equals(((Named) obj).value());
    }

    @Override
    public String toString() {
      return toString;
    }
  }

}
