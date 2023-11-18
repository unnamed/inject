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
package team.unnamed.inject.provision;

import team.unnamed.inject.Provider;
import team.unnamed.inject.impl.BinderImpl;
import team.unnamed.inject.impl.InjectorImpl;
import team.unnamed.inject.impl.ProvisionStack;
import team.unnamed.inject.key.Key;
import team.unnamed.inject.scope.Scope;
import team.unnamed.inject.util.Validate;

import java.util.Objects;

/**
 * Provider wrapper used for user-provided providers
 * (lowest level of library usage). Providers should
 * be wrapped because we need to store the 'injected'
 * state in providers (providers should be injected
 * only once)
 *
 * @param <T> The provider return type
 */
public class DelegatingStdProvider<T>
        extends StdProvider<T>
        implements Provider<T> {

    private final Provider<T> delegate;

    public DelegatingStdProvider(Provider<T> delegate) {
        this.delegate = Validate.notNull(delegate, "delegate");
    }

    public DelegatingStdProvider(boolean injected, Provider<T> delegate) {
        this(delegate);
        this.setInjected(injected);
    }

    public Provider<T> getDelegate() {
        return delegate;
    }

    @Override
    public void inject(ProvisionStack stack, InjectorImpl injector) {
        Providers.inject(stack, injector, delegate);
        injected = true;
    }

    @Override
    public boolean onBind(BinderImpl binder, Key<?> key) {
        if (delegate instanceof StdProvider) {
            return ((StdProvider<?>) delegate).onBind(binder, key);
        } else {
            return true;
        }
    }

    @Override
    public Provider<T> withScope(Key<?> match, Scope scope) {
        if (delegate instanceof StdProvider) {
            return ((StdProvider<T>) delegate).withScope(match, scope);
        } else {
            return super.withScope(match, scope);
        }
    }

    @Override
    public T get() {
        return delegate.get();
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DelegatingStdProvider)) return false;
        DelegatingStdProvider<?> that = (DelegatingStdProvider<?>) o;
        return (that.isInjected() == isInjected())
                && Objects.equals(delegate, that.delegate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isInjected(), delegate);
    }

}
