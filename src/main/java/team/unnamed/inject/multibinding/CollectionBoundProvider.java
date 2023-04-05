package team.unnamed.inject.multibinding;

import team.unnamed.inject.Provider;
import team.unnamed.inject.impl.InjectorImpl;
import team.unnamed.inject.impl.ProvisionStack;
import team.unnamed.inject.key.Key;
import team.unnamed.inject.key.TypeReference;
import team.unnamed.inject.provision.StdProvider;

import java.util.Collection;
import java.util.Collections;

/**
 * Represents a Collection provider that delegates all the
 * injection to the element providers.
 *
 * @param <E> The element type
 */
class CollectionBoundProvider<E>
        extends StdProvider<Collection<E>> {

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
    public void inject(ProvisionStack stack, InjectorImpl injector) {
        for (Provider<? extends E> provider : delegates) {
            if (provider instanceof StdProvider) {
                ((StdProvider<?>) provider).inject(stack, injector);
            } else {
                injector.injectMembers(
                        stack,
                        Key.of(TypeReference.of(provider.getClass())),
                        provider
                );
            }
        }
        injected = true;
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

    /**
     * Internal method for getting the providers without wrapping the collection
     */
    Collection<Provider<? extends E>> getModifiableProviderCollection() {
        return delegates;
    }

    @Override
    public String toString() {
        return "CollectionMultiBound(" + delegates + ")";
    }

}
