package me.yushust.inject.impl;

import me.yushust.inject.util.Validate;

import javax.inject.Named;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Collection of factory static methods and other util
 * methods for ease the handling of qualifiers
 */
public final class Annotations {

  private Annotations() {
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

  @SuppressWarnings("ClassExplicitlyAnnotation")
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
