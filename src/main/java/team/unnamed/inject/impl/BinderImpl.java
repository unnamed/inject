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
import team.unnamed.inject.Module;
import team.unnamed.inject.Provider;
import team.unnamed.inject.error.BindingException;
import team.unnamed.inject.error.ErrorAttachableImpl;
import team.unnamed.inject.key.Key;
import team.unnamed.inject.key.TypeReference;
import team.unnamed.inject.multibinding.MultiBindingBuilderImpl;
import team.unnamed.inject.provision.Providers;
import team.unnamed.inject.provision.StdProvider;
import team.unnamed.inject.provision.std.MethodAsProvider;
import team.unnamed.inject.provision.std.generic.impl.TypeReferenceGenericProvider;
import team.unnamed.inject.util.Validate;

import java.util.HashMap;
import java.util.Map;

public class BinderImpl extends ErrorAttachableImpl implements Binder {

    private final Map<Key<?>, Provider<?>> bindings =
            new HashMap<>();

    public BinderImpl() {
        // soft
        bind(TypeReference.class).toGenericProvider(new TypeReferenceGenericProvider()).singleton();
    }

    public <T> StdProvider<T> getProvider(Key<T> key) {
        // it's safe, the providers are setted
        // after (provider -> injected provider) conversion
        @SuppressWarnings("unchecked")
        StdProvider<T> provider =
                (StdProvider<T>) this.bindings.get(key);
        return provider;
    }

    @Override
    public void $unsafeBind(Key<?> key, Provider<?> provider) {
        Validate.notNull(key, "key");
        Validate.notNull(provider, "provider");
        if (!(provider instanceof StdProvider) || ((StdProvider<?>) provider).onBind(this, key)) {
            this.bindings.put(key, Providers.normalize(provider));
        }
    }

    @Override
    public <T> QualifiedBindingBuilder<T> bind(TypeReference<T> keyType) {
        return new BindingBuilderImpl<>(this, keyType);
    }

    @Override
    public <T> Binder.MultiBindingBuilder<T> multibind(TypeReference<T> keyType) {
        return new MultiBindingBuilderImpl<>(this, keyType);
    }

    /**
     * Throws the errors attached to this attachable
     */
    @Override
    public void reportAttachedErrors() {
        if (hasErrors()) {
            throw new BindingException(formatMessages());
        }
    }

    @Override
    public void install(Iterable<? extends Module> modules) {
        for (Module module : modules) {
            // configure the manual bindings
            module.configure(this);

            // resolve the provider methods
            MethodAsProvider.resolveMethodProviders(
                    this,
                    TypeReference.of(module.getClass()),
                    module
            ).forEach(this::$unsafeBind);
        }
    }

}
