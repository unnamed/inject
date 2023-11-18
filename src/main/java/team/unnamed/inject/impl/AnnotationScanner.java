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

import team.unnamed.inject.ProvidedBy;
import team.unnamed.inject.Provider;
import team.unnamed.inject.Singleton;
import team.unnamed.inject.Targetted;
import team.unnamed.inject.key.Key;
import team.unnamed.inject.key.TypeReference;
import team.unnamed.inject.provision.Providers;
import team.unnamed.inject.provision.StdProvider;
import team.unnamed.inject.scope.Scope;
import team.unnamed.inject.scope.Scopes;

import java.lang.reflect.Modifier;

/**
 * Scans a type looking for scope annotations
 * and binding annotations like {@link Singleton},
 * {@link Targetted}, {@link ProvidedBy}.
 */
final class AnnotationScanner {

    private AnnotationScanner() {
    }

    /**
     * Scans the specified type if it's not bound. It binds the type its annotations
     */
    static <T> void bind(TypeReference<T> keyType, BinderImpl binder) {

        Key<T> key = Key.of(keyType);
        StdProvider<? extends T> provider = binder.getProvider(key);

        // it's already explicit-bound
        if (provider != null) {
            return;
        }

        Class<? super T> rawType = keyType.getRawType();

        Targetted target = rawType.getAnnotation(Targetted.class);
        ProvidedBy providedBy = rawType.getAnnotation(ProvidedBy.class);

        if (target != null) {
            Key<? extends T> linkedKey = Key.of(TypeReference.of(target.value()));
            binder.$unsafeBind(key, Providers.link(key, linkedKey));
        } else if (providedBy != null) {
            TypeReference<? extends Provider<? extends T>> linkedProvider =
                    TypeReference.of(providedBy.value());
            binder.$unsafeBind(key, Providers.providerTypeProvider(linkedProvider));
        }
    }

    /**
     * Scopes the specified type using its annotations.
     */
    static <T> void scope(TypeReference<T> keyType, BinderImpl binder) {

        Key<T> key = Key.of(keyType);
        StdProvider<? extends T> provider = binder.getProvider(key);

        Class<? super T> rawType = keyType.getRawType();

        // so it can be linked to itself
        if (provider == null && !rawType.isInterface()
                && !Modifier.isAbstract(rawType.getModifiers())) {
            // link to self
            provider = Providers.normalize(Providers.link(key, key));
        }

        // if there's no a provider it cannot
        // be scoped!
        if (provider == null) {
            return;
        }

        Scope scope = Scopes.getScanner().scan(rawType);
        if (scope != Scopes.NONE) {
            binder.$unsafeBind(key, provider.withScope(key, scope));
        }
    }

}
