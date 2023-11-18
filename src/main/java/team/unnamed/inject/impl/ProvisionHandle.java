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

import team.unnamed.inject.key.Key;
import team.unnamed.inject.provision.Providers;
import team.unnamed.inject.provision.StdProvider;
import team.unnamed.inject.provision.std.ScopedProvider;
import team.unnamed.inject.provision.std.generic.ToGenericProvider;

public class ProvisionHandle {

    private final InjectorImpl injector;
    private final BinderImpl binder;

    public ProvisionHandle(
            InjectorImpl injector,
            BinderImpl binder
    ) {
        this.injector = injector;
        this.binder = binder;
    }

    private <T> StdProvider<T> getGenericProvider(Class<T> rawType, Key<T> match) {

        Key<T> rawTypeKey = Key.of(rawType);

        StdProvider<T> provider = binder.getProvider(rawTypeKey);

        if (provider instanceof ScopedProvider) {
            ScopedProvider<T> scopedProvider = (ScopedProvider<T>) provider;
            if (scopedProvider.requiresJitScoping()) {
                provider = (StdProvider<T>) scopedProvider
                        .withScope(match, scopedProvider.getScope());
                binder.$unsafeBind(match, provider);
            }
        }

        if (!(provider instanceof ToGenericProvider.SyntheticGenericProvider)) {
            return null;
        } else {
            return provider;
        }
    }

    public <T> StdProvider<T> getProviderAndInject(ProvisionStack stack, Key<T> key) {
        StdProvider<T> provider = binder.getProvider(key);
        if (provider == null) {
            Class<T> rawType = key.getType().getRawType();
            if (key.getType().getType() != rawType) {
                if ((provider = getGenericProvider(rawType, key)) == null) {
                    return null;
                }
            } else {
                return null;
            }
        }
        if (!provider.isInjected()) {
            Providers.inject(stack, injector, provider);
        }
        return provider;
    }

}
