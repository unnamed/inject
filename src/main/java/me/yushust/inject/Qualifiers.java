package me.yushust.inject;

import me.yushust.inject.util.Validate;

import javax.inject.Named;
import java.lang.annotation.Annotation;

/**
 * Collection of factory static methods
 * for ease the creation of qualifiers
 */
public final class Qualifiers {

  private Qualifiers() {
    throw new UnsupportedOperationException("This class couldn't be instantiated!");
  }

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

    private NamedImpl(String name) {
      this.name = name;
      this.hashCode = (127 * "value".hashCode()) ^ name.hashCode();
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
      return "@" + Named.class.getSimpleName() + " {"
          + "name=" + name
          + "}";
    }
  }

}
