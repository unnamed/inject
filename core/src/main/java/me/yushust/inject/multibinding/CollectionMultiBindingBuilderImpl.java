package me.yushust.inject.multibinding;

import me.yushust.inject.Binder;
import me.yushust.inject.assisted.ValueFactory;
import me.yushust.inject.internal.BinderImpl;
import me.yushust.inject.internal.LinkedBuilder;
import me.yushust.inject.key.Key;
import me.yushust.inject.provision.Providers;
import me.yushust.inject.provision.StdProvider;
import me.yushust.inject.scope.Scope;
import me.yushust.inject.util.Validate;

import javax.inject.Provider;
import java.util.Collection;

/**
 * Represents a Collection Binding Builder, with this
 * builder you add the element providers
 *
 * @param <E> The type of the elements
 */
class CollectionMultiBindingBuilderImpl<E> implements
    Binder.CollectionMultiBindingBuilder<E>,
    LinkedBuilder<Binder.CollectionMultiBindingBuilder<E>, E> {

  private final BinderImpl binder;
  private final Key<? extends Collection<E>> collectionKey;
  private final Key<E> elementKey;

  private final CollectionCreator collectionCreator;

  public CollectionMultiBindingBuilderImpl(BinderImpl binder, Key<? extends Collection<E>> collectionKey,
                                              Key<E> elementKey, CollectionCreator collectionCreator) {
    this.binder = binder;
    this.collectionKey = collectionKey;
    this.elementKey = elementKey;
    this.collectionCreator = collectionCreator;
  }

  @Override
  public void in(Scope scope) {
    Validate.notNull(scope, "scope");
    StdProvider<? extends Collection<E>> provider = binder.getProvider(collectionKey);
    if (provider != null) {
      binder.$unsafeBind(collectionKey, Providers.scope(provider, scope));
    }
  }

  @Override
  public Key<E> key() {
    return elementKey;
  }

  @Override
  public Binder.CollectionMultiBindingBuilder<E> toProvider(Provider<? extends E> provider) {

    Validate.notNull(provider, "provider");
    StdProvider<? extends Collection<E>> collectionProvider = binder.getProvider(collectionKey);

    if (collectionProvider == null) {
      collectionProvider = new CollectionBoundProvider<>(collectionCreator);
      binder.$unsafeBind(collectionKey, collectionProvider);
    }

    Provider<? extends Collection<E>> delegate = collectionProvider;
    if (!(delegate instanceof CollectionBoundProvider)) {
      throw new IllegalStateException("The key '" + collectionKey
          + "' is already bound and it isn't a multibinding!");
    }
    @SuppressWarnings("unchecked")
    CollectionBoundProvider<E> collectionDelegate =
        (CollectionBoundProvider<E>) delegate;
    collectionDelegate.getModifiableProviderCollection().add(provider);
    return this;
  }

  @Override
  public void toFactory(Class<? extends ValueFactory> factory) {

  }

  @Override
  public Binder.CollectionMultiBindingBuilder<E> toInstance(E instance) {
    return toProvider(Providers.instanceProvider(elementKey, instance));
  }

}
