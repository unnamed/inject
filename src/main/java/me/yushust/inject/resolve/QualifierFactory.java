package me.yushust.inject.resolve;

import me.yushust.inject.key.Qualifier;

import java.lang.annotation.Annotation;

public interface QualifierFactory {

  Qualifier getQualifier(Class<? extends Annotation> annotationType);

  Qualifier getQualifier(Annotation annotation);

}
