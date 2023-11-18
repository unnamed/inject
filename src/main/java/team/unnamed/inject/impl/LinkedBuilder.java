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
import team.unnamed.inject.assisted.ValueFactory;
import team.unnamed.inject.assisted.provision.ToFactoryProvider;
import team.unnamed.inject.key.Key;
import team.unnamed.inject.key.TypeReference;
import team.unnamed.inject.provision.Providers;
import team.unnamed.inject.provision.std.generic.GenericProvider;
import team.unnamed.inject.provision.std.generic.ToGenericProvider;
import team.unnamed.inject.util.Validate;

public interface LinkedBuilder<R, T> extends Binder.Linked<R, T> {

    Key<T> key();

    @Override
    default R toGenericProvider(GenericProvider<? extends T> provider) {
        Validate.notNull(provider, "provider");
        return toProvider(new ToGenericProvider<>(provider));
    }

    @Override
    default void toFactory(TypeReference<? extends ValueFactory> factory) {
        Validate.notNull(factory, "factory");
        toProvider(new ToFactoryProvider<>(factory));
    }

    @Override
    default R to(TypeReference<? extends T> targetType) {
        Validate.notNull(targetType, "targetType");
        return toProvider(Providers.link(key(), Key.of(targetType)));
    }

    @Override
    default <P extends Provider<? extends T>> R toProvider(TypeReference<P> providerClass) {
        Validate.notNull(providerClass, "providerClass");
        return toProvider(Providers.providerTypeProvider(providerClass));
    }

}
