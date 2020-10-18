package me.yushust.inject.internal;

import me.yushust.inject.Binder;
import me.yushust.inject.Qualifiers;
import me.yushust.inject.key.Key;
import me.yushust.inject.key.Qualifier;
import me.yushust.inject.resolve.QualifierFactory;
import me.yushust.inject.util.Validate;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

/**
 * Removes the responsibility to the implementer class
 * of implement this methods. This interface behaves
 * like an abstract class (it's not an abstract class
 * because sometimes we need multiple "super-classes")
 */
interface KeyBuilder<R, T> extends Binder.Qualified<R> {

  QualifierFactory factory();

  Key<T> key();

  void setKey(Key<T> key);

  @Override
  default R markedWith(Class<? extends Annotation> qualifierType) {
    Validate.notNull(qualifierType, "qualifierType");
    qualified(factory().getQualifier(qualifierType));
    return getReturnValue();
  }

  @Override
  default R qualified(Annotation annotation) {
    Validate.notNull(annotation, "annotation");
    qualified(factory().getQualifier(annotation));
    return getReturnValue();
  }

  @Override
  default R named(String name) {
    Validate.notNull(name, "name");
    return qualified(Qualifiers.createNamed(name));
  }

  default void qualified(Qualifier qualifier) {
    Validate.notNull(qualifier, "qualifier");
    Set<Qualifier> qualifiers = new HashSet<>(
        key().getQualifiers()
    );
    qualifiers.add(qualifier);
    setKey(new Key<>(key().getType(), qualifiers));
  }

  R getReturnValue();

}
