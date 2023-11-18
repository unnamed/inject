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
import team.unnamed.inject.assisted.provision.ToFactoryProvider;
import team.unnamed.inject.impl.BinderImpl;
import team.unnamed.inject.impl.InjectorImpl;
import team.unnamed.inject.impl.ProvisionStack;
import team.unnamed.inject.key.Key;
import team.unnamed.inject.provision.std.ScopedProvider;
import team.unnamed.inject.scope.Scope;

public abstract class StdProvider<T> implements Provider<T> {

    protected boolean injected; // TODO: Make this atomic

    public boolean isInjected() {
        return injected;
    }

    public void setInjected(boolean injected) {
        this.injected = injected;
    }

    /**
     * Adds the control of scoping the provider. Used by scoped
     * providers (that check if the scope is the same, and return
     * the same provider) and some other providers like InstanceProvider
     * that doesn't support scopes and throws an exception.
     * <p>
     * Scopes the provider with the given {@code scope}
     *
     * @return The scope applied to the provider, the providers
     * are externally immutable, so this shouldn't modify the
     * real provider and returns another provider or the same provider
     */
    public Provider<T> withScope(Key<?> match, Scope scope) {
        StdProvider<T> scopedProvider = new ScopedProvider<>(this, scope);
        scopedProvider.injected = injected;
        return scopedProvider;
    }

    /**
     * <p>Used by standard providers and some other
     * providers to delegate the injection to the
     * wrapped provider (when it's a wrapper provider)
     * or injecting some provider properties without
     * checking everything</p>
     *
     * @param stack    The thread injection stack (passed as argument
     *                 instead of getting it from the thread-local held
     *                 by the injector)
     * @param injector The injector used to inject this provider
     */
    public void inject(ProvisionStack stack, InjectorImpl injector) {
        injected = true;
        injector.injectMembers(this);
    }

    /**
     * Listener method called when the provider
     * is bound. Commonly this is called immediately
     * after the construction.
     *
     * <p>Adds the control of the binding to the provider,
     * used by standard providers to replace bindings and
     * some other things.</p>
     *
     * @param binder The binder used to bind this provider
     * @param key    The bound key
     * @return False if the binding must be removed (handled
     * by the binder) this is used for special providers like
     * the {@link ToFactoryProvider} that converts its binding
     * to another binding. (Removes its binding and adds another one)
     */
    public boolean onBind(BinderImpl binder, Key<?> key) {
        return true;
    }

    @Override
    public T get() {
        return null;
    }

    public T get(Key<?> match) {
        return get();
    }

}
