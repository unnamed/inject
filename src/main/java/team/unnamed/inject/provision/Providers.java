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
import team.unnamed.inject.impl.InjectorImpl;
import team.unnamed.inject.impl.ProvisionStack;
import team.unnamed.inject.key.Key;
import team.unnamed.inject.key.TypeReference;
import team.unnamed.inject.provision.std.InstanceProvider;
import team.unnamed.inject.provision.std.LinkedProvider;
import team.unnamed.inject.provision.std.ProviderTypeProvider;
import team.unnamed.inject.provision.std.ScopedProvider;
import team.unnamed.inject.util.Validate;

/**
 * Collection of static factory methods to create providers
 */
public final class Providers {

    private Providers() {
    }

    public static void inject(ProvisionStack stack, InjectorImpl injector, Provider<?> provider) {
        if (provider instanceof StdProvider) {
            ((StdProvider<?>) provider).inject(stack, injector);
        } else {
            injector.injectMembers(stack, Key.of(TypeReference.of(provider.getClass())), provider);
        }
    }

    public static <T> Provider<T> unwrap(Provider<T> provider) {
        if (provider instanceof DelegatingStdProvider) {
            return unwrap(((DelegatingStdProvider<T>) provider).getDelegate());
        } else if (provider instanceof ScopedProvider) {
            return unwrap(((ScopedProvider<T>) provider).getUnscoped());
        } else {
            return provider;
        }
    }

    public static <T> StdProvider<T> normalize(Provider<T> provider) {
        if (provider instanceof StdProvider) {
            return (StdProvider<T>) provider;
        } else {
            return new DelegatingStdProvider<>(provider);
        }
    }

    public static <T> Provider<? extends T> instanceProvider(Key<T> key, T instance) {
        Validate.notNull(key, "key");
        Validate.notNull(instance, "instance");
        return new InstanceProvider<>(instance);
    }

    public static <T> Provider<? extends T> providerTypeProvider(TypeReference<? extends Provider<? extends T>> providerClass) {
        Validate.notNull(providerClass);
        return new ProviderTypeProvider<>(providerClass);
    }

    public static <T> Provider<? extends T> link(Key<T> key, Key<? extends T> target) {
        Validate.notNull(key, "key");
        Validate.notNull(target, "target");
        return new LinkedProvider<>(key, target);
    }

}
