package me.yushust.inject.multibinding;

import me.yushust.inject.impl.InjectorImpl;
import me.yushust.inject.impl.ProvisionStack;
import me.yushust.inject.key.Key;
import me.yushust.inject.key.TypeReference;
import me.yushust.inject.provision.StdProvider;
import me.yushust.inject.provision.ioc.InjectionListener;

import javax.inject.Provider;
import java.util.Collection;
import java.util.Collections;

/**
 * Represents a Collection provider that delegates all the
 * injection to the element providers.
 *
 * @param <E> The element type
 */
class CollectionBoundProvider<E>
    extends StdProvider<Collection<E>>
    implements InjectionListener {

  private final Collection<Provider<? extends E>> delegates;
  private final CollectionCreator collectionCreator;

  CollectionBoundProvider(CollectionCreator collectionCreator) {
    this.collectionCreator = collectionCreator;
    this.delegates = collectionCreator.create();
  }

  /**
   * Injects members of all element providers
   */
  @Override
  public void onInject(ProvisionStack stack, InjectorImpl injector) {
    for (Provider<? extends E> provider : delegates) {
      if (provider instanceof InjectionListener) {
        ((InjectionListener) provider).onInject(stack, injector);
      } else {
        injector.injectMembers(
            stack,
            Key.of(TypeReference.of(provider.getClass())),
            provider
        );
      }
    }
    setInjected(true);
  }

  @Override
  public Collection<E> get() {
    Collection<E> collection = collectionCreator.create();
    for (Provider<? extends E> delegate : delegates) {
      collection.add(delegate.get());
    }
    return collection;
  }

  public Collection<Provider<? extends E>> getProviders() {
    return Collections.unmodifiableCollection(delegates);
  }

  /** Internal method for getting the providers without wrapping the collection */
  Collection<Provider<? extends E>> getModifiableProviderCollection() {
    return delegates;
  }

  @Override
  public String toString() {
    return "CollectionMultiBound(" + delegates + ")";
  }
}
