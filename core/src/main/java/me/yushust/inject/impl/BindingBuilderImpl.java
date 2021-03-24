package me.yushust.inject.impl;

import me.yushust.inject.Binder;
import me.yushust.inject.assisted.provision.ToFactoryProvider;
import me.yushust.inject.assisted.ValueFactory;
import me.yushust.inject.key.Key;
import me.yushust.inject.key.TypeReference;
import me.yushust.inject.provision.Providers;
import me.yushust.inject.scope.Scope;
import me.yushust.inject.util.Validate;

import javax.inject.Provider;

class BindingBuilderImpl<T> implements
    Binder.QualifiedBindingBuilder<T>,
    KeyBuilder<Binder.QualifiedBindingBuilder<T>, T>,
    LinkedBuilder<Binder.Scoped,T> {

  private Key<T> key;
  private final BinderImpl binder;

  protected BindingBuilderImpl(BinderImpl binder, TypeReference<T> key) {
    this.key = Key.of(key);
    this.binder = binder;
  }

  @Override
  public void in(Scope scope) {
    Validate.notNull(scope, "scope");
    selfBindingIfNotBound();
    binder.$unsafeBind(
        key,
        binder.getProvider(key)
            .withScope(key, scope)
    );
  }

  @Override
  public Binder.Scoped toProvider(Provider<? extends T> provider) {
    Validate.notNull(provider, "provider");
    requireNotBound();
    binder.$unsafeBind(key, provider);
    return this;
  }

  @Override
  public void toFactory(Class<? extends ValueFactory> factory) {
    Validate.notNull(factory, "factory");
    requireNotBound();
    binder.$unsafeBind(key, new ToFactoryProvider<>(factory));
  }

  @Override
  public void toInstance(T instance) {
    Validate.notNull(instance, "instance");
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
