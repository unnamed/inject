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
package team.unnamed.inject.provision.std;

import team.unnamed.inject.Provider;
import team.unnamed.inject.impl.InjectorImpl;
import team.unnamed.inject.impl.ProvisionStack;
import team.unnamed.inject.key.Key;
import team.unnamed.inject.provision.Providers;
import team.unnamed.inject.provision.StdProvider;
import team.unnamed.inject.scope.Scope;
import team.unnamed.inject.util.Validate;

/**
 * It's a provider wrapped. Maintains the
 * unscoped provider, the scoped provider
 * and the scope.
 *
 * <p>The providers cannot be re-scoped</p>
 *
 * @param <T> The provider return type
 */
public class ScopedProvider<T>
        extends StdProvider<T>
        implements Provider<T> {

    private final Provider<T> unscoped;
    private final Provider<T> scoped;
    private final Scope scope;

    public ScopedProvider(Provider<T> provider, Scope scope) {
        this.unscoped = Validate.notNull(provider, "provider");
        this.scope = Validate.notNull(scope, "scope");
        this.scoped = scope.scope(provider);
    }

    protected ScopedProvider() {
        this.unscoped = null;
        this.scoped = null;
        this.scope = null;
    }

    @Override
    public Provider<T> withScope(Key<?> match, Scope scope) {
        if (this.scope == scope) {
            return this;
        }
        throw new UnsupportedOperationException(
                "Cannot scope the provider again! Scope: " + scope.getClass().getSimpleName()
                        + ". Provider: " + unscoped
        );
    }

    @Override
    public void inject(ProvisionStack stack, InjectorImpl injector) {
        Providers.inject(stack, injector, unscoped);
        Providers.inject(stack, injector, scoped);
        injected = true;
    }

    @Override
    public T get() {
        return scoped.get();
    }

    public Provider<T> getUnscoped() {
        return unscoped;
    }

    public Provider<T> getScoped() {
        return scoped;
    }

    public Scope getScope() {
        return scope;
    }

    public boolean requiresJitScoping() {
        return false;
    }

}
