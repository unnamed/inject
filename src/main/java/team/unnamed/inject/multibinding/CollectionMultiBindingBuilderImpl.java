/*
 * This file is part of inject, licensed under the MIT license
 *
 * Copyright (c) 2021-2023 Unnamed Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
