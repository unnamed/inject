package team.unnamed.inject.multibinding;

import team.unnamed.inject.Binder;
import team.unnamed.inject.Provider;
import team.unnamed.inject.impl.BinderImpl;
import team.unnamed.inject.impl.LinkedBuilder;
import team.unnamed.inject.key.Key;
import team.unnamed.inject.provision.Providers;
import team.unnamed.inject.provision.StdProvider;
import team.unnamed.inject.scope.Scope;
import team.unnamed.inject.util.Validate;

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
    @SuppressWarnings("unchecked")
    public void in(Scope scope) {
        Validate.notNull(scope, "scope");
        Provider<? extends Collection<E>> provider = Providers.unwrap(binder.getProvider(collectionKey));
        if (provider != null) {
            if (provider instanceof StdProvider) {
                provider = ((StdProvider<? extends Collection<E>>) provider)
                        .withScope(collectionKey, scope);
            } else {
                provider = scope.scope(provider);
            }
            binder.$unsafeBind(collectionKey, provider);
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

        Provider<? extends Collection<E>> delegate = Providers.unwrap(collectionProvider);
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
    public Binder.CollectionMultiBindingBuilder<E> toInstance(E instance) {
        return toProvider(Providers.instanceProvider(elementKey, instance));
    }

}
