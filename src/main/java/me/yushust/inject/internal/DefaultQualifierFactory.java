package me.yushust.inject.internal;

import me.yushust.inject.Qualifiers;
import me.yushust.inject.key.Qualifier;
import me.yushust.inject.resolve.QualifierFactory;
import me.yushust.inject.util.Validate;

import java.lang.annotation.Annotation;

public enum DefaultQualifierFactory implements QualifierFactory {

  INSTANCE;

  @Override
  public Qualifier getQualifier(Class<? extends Annotation> annotationType) {
    return new ClassQualifier(annotationType);
  }

  @Override
  public Qualifier getQualifier(Annotation annotation) {
    if (Qualifiers.containsOnlyDefaultValues(annotation)) {
      return new ClassQualifier(annotation.annotationType());
    } else {
      return new InstanceQualifier(annotation);
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
    public String toString() {
      return "marked with @" + annotationType.getSimpleName();
    }
  }

}
