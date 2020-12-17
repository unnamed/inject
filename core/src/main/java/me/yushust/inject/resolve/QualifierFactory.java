package me.yushust.inject.resolve;

import me.yushust.inject.Qualifiers;
import me.yushust.inject.key.Qualifier;
import me.yushust.inject.util.Validate;

import java.lang.annotation.Annotation;

public class QualifierFactory {

  public static Qualifier getQualifier(Class<? extends Annotation> annotationType) {
    return new ClassQualifier(annotationType);
  }

  public static Qualifier getQualifier(Annotation annotation) {
    if (!Qualifiers.containsOnlyDefaultValues(annotation)) {
      return new InstanceQualifier(annotation);
    } else {
      return new ClassQualifier(annotation.annotationType());
    }
  }

  private static class InstanceQualifier implements Qualifier {

    private final Annotation annotation;

    private InstanceQualifier(Annotation annotation) {
      this.annotation = annotation;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      InstanceQualifier that = (InstanceQualifier) o;
      return annotation.equals(that.annotation);
    }

    @Override
    public int hashCode() {
      return annotation.hashCode();
    }

    @Override
    public Object raw() {
      return annotation;
    }

    @Override
    public String toString() {
      return "qualified with " + Qualifiers.annotationToString(annotation);
    }

  }

  private static class ClassQualifier implements Qualifier {

    private final Class<? extends Annotation> annotationType;

    private ClassQualifier(Class<? extends Annotation> annotationType) {
      this.annotationType = Validate.notNull(annotationType);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      ClassQualifier that = (ClassQualifier) o;
      return annotationType.equals(that.annotationType);
    }

    @Override
    public int hashCode() {
      return annotationType.hashCode();
    }

    @Override
    public Object raw() {
      return annotationType;
    }

    @Override
    public String toString() {
      return "marked with @" + annotationType.getSimpleName();
    }
  }

}
