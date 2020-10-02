package me.yushust.inject.util;

import me.yushust.inject.key.Qualifier;
import me.yushust.inject.resolve.QualifierFactory;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

public final class Annotations {

  private Annotations() {
    throw new UnsupportedOperationException("This class couldn't be instantiated!");
  }

  public static Set<Qualifier> getQualifiers(QualifierFactory factory, Annotation[] annotations) {
    Set<Qualifier> qualifiers = new HashSet<Qualifier>();
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

}
