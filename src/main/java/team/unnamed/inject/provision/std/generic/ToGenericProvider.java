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
package team.unnamed.inject.provision.std.generic;

import team.unnamed.inject.Provider;
import team.unnamed.inject.impl.BinderImpl;
import team.unnamed.inject.impl.InjectorImpl;
import team.unnamed.inject.impl.ProvisionStack;
import team.unnamed.inject.key.Key;
import team.unnamed.inject.provision.StdProvider;
import team.unnamed.inject.provision.std.ScopedProvider;
import team.unnamed.inject.scope.Scope;
import team.unnamed.inject.util.Validate;

public class ToGenericProvider<T>
        extends ScopedProvider<T>
        implements Provider<T> {

    private final GenericProvider<T> provider;
    private Scope scope;

    public ToGenericProvider(GenericProvider<T> provider) {
        this.provider = Validate.notNull(provider, "provider");
    }

    @Override
    public void inject(ProvisionStack stack, InjectorImpl injector) {
        // don't inject null references
        injected = true;
    }

    @Override
    public boolean onBind(BinderImpl binder, Key<?> key) {

        boolean isRawType = key.isPureRawType();

        if (!isRawType) {
            binder.attach("You must bound the raw-type to a GenericProvider, " +
                    "not a parameterized type! (key: " + key + ", genericProvider: " + provider + ")");
        }

        return isRawType;
    }

    @Override
    public T get() {
        throw new IllegalStateException("Key was bound to a generic provider," +
                " it cannot complete a raw-type!\n\tProvider: " + provider);
    }

    /**
     * Special injector case for keys bound
     * to generic providers
     */
    @Override
    public T get(Key<?> bound) {
        return provider.get(bound);
    }

    @Override
    public Provider<T> withScope(Key<?> match, Scope scope) {
        if (scope != null) {
            this.scope = scope;
        }
        if (match.isPureRawType()) {
            return this;
        } else {
            return new SyntheticGenericProvider(
                    match,
                    scope == null ? this.scope : scope
            );
        }
    }

    @Override
    public boolean requiresJitScoping() {
        return true;
    }

    public class SyntheticGenericProvider
            extends StdProvider<T>
            implements Provider<T> {

        private final Scope scope;
        private final Provider<T> scoped;

        public SyntheticGenericProvider(Key<?> match, Scope scope) {
            this.scope = scope;
            Provider<T> unscoped = ToGenericProvider.this.provider.asConstantProvider(match);
            this.scoped = scope == null ? unscoped : scope.scope(unscoped);
            setInjected(true);
        }

        @Override
        public T get() {
            return scoped.get();
        }

        @Override
        public Provider<T> withScope(Key<?> match, Scope scope) {
            Validate.argument(this.scope == scope, "Not the same scope on GenericProvider!");
            return new SyntheticGenericProvider(match, scope);
        }

    }

}
