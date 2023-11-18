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
package team.unnamed.inject.impl;

import team.unnamed.inject.Binder;
import team.unnamed.inject.Provider;
import team.unnamed.inject.key.Key;
import team.unnamed.inject.key.TypeReference;
import team.unnamed.inject.provision.Providers;
import team.unnamed.inject.scope.Scope;
import team.unnamed.inject.util.Validate;

class BindingBuilderImpl<T> implements
        Binder.QualifiedBindingBuilder<T>,
        KeyBuilder<Binder.QualifiedBindingBuilder<T>, T>,
        LinkedBuilder<Binder.Scoped, T> {

    private final BinderImpl binder;
    private Key<T> key;

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
