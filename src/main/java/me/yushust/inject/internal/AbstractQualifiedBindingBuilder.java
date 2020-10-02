package me.yushust.inject.internal;

import me.yushust.inject.Binder;
import me.yushust.inject.Qualifiers;
import me.yushust.inject.key.Qualifier;
import me.yushust.inject.key.TypeReference;
import me.yushust.inject.resolve.QualifierFactory;
import me.yushust.inject.scope.Scopes;

import javax.inject.Provider;
import java.lang.annotation.Annotation;

/**
 * Abstract class that implements {@link Binder.Qualified} and
 * methods that delegates the functionality to another method.
 *
 * <p>This abstract class just removes the responsibility
 * of creating method that calls another methods</p>
 */
public abstract class AbstractQualifiedBindingBuilder<T> implements Binder.Qualified<T> {

  protected final QualifierFactory qualifierFactory;

  protected AbstractQualifiedBindingBuilder(QualifierFactory qualifierFactory) {
    this.qualifierFactory = qualifierFactory;
  }

  /** Converts the qualifierType to a real qualifier */
  public Binder.Qualified<T> markedWith(Class<? extends Annotation> qualifierType) {
    qualified(qualifierFactory.getQualifier(qualifierType));
    return this;
  }

  /** Converts the annotation to a resolvable qualifier */
  public Binder.Qualified<T> qualified(Annotation annotation) {
    qualified(qualifierFactory.getQualifier(annotation));
    return this;
  }

  /** Method alias for {@link Binder.Scoped#in}({@link Scopes#SINGLETON})*/
  public void singleton() {
    in(Scopes.SINGLETON);
  }

  /** Method alias for {@link Binder.Qualified#qualified}({@link Qualifiers#createNamed}({@code name}))*/
  public Binder.Qualified<T> named(String name) {
    return qualified(Qualifiers.createNamed(name));
  }

  /** Method alias for {@link Binder.Qualified#to(TypeReference)}*/
  public Binder.Scoped to(Class<? extends T> targetType) {
    return to(TypeReference.<T>of(targetType));
  }

  /** Adds a qualifier to the constructing key */
  protected abstract void qualified(Qualifier qualifier);

  /** Method alias for {@link Binder.Qualified#toProvider(TypeReference)} */
  public <P extends Provider<? extends T>> Binder.Scoped toProvider(Class<P> providerClass) {
    return toProvider(TypeReference.<P>of(providerClass));
  }

}
