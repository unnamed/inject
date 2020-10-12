package me.yushust.inject.internal;

import me.yushust.inject.Binder;
import me.yushust.inject.key.Key;
import me.yushust.inject.key.TypeReference;
import me.yushust.inject.resolve.QualifierFactory;
import me.yushust.inject.scope.Scope;
import me.yushust.inject.util.Validate;

import javax.inject.Provider;

class BindingBuilderImpl<T> implements
    Binder.QualifiedBindingBuilder<T>,
    KeyBuilder<Binder.QualifiedBindingBuilder<T>, T>,
    LinkedBuilder<Binder.Scoped,T> {

  private final QualifierFactory qualifierFactory;
  private Key<T> key;
  private final BinderImpl binder;

  protected BindingBuilderImpl(QualifierFactory factory, BinderImpl binder, TypeReference<T> key) {
    this.qualifierFactory = factory;
    this.key = Key.of(key);
    this.binder = binder;
  }

  @Override
  public void in(Scope scope) {
    Validate.notNull(scope, "scope");
    selfBindingIfNotBound();
    binder.bindTo(
        key,
        binder
            .getProvider(key)
            .withScope(scope)
    );
  }

  @Override
  public Binder.Scoped toProvider(Provider<? extends T> provider) {
    requireNotBound();
    binder.bindTo(key, provider);
    return this;
  }

  @Override
  public void toInstance(T instance) {
    toProvider(Providers.instanceProvider(key, instance));
  }

  private void requireNotBound() {
    if (binder.getProvider(key) != null) {
      throw new IllegalStateException("The key is already bound");
    }
  }

  private void selfBindingIfNotBound() {
    if (binder.getProvider(key) == null) {
      toProvider(Providers.link(key, key));
    }
  }

  @Override
  public QualifierFactory factory() {
    return qualifierFactory;
  }

  @Override
  public Key<T> key() {
    return key;
  }

  @Override
  public void setKey(Key<T> key) {
    this.key = key;
  }

  @Override
  public Binder.QualifiedBindingBuilder<T> getReturnValue() {
    return this;
  }

}
