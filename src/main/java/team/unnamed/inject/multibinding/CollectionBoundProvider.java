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
