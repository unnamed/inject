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
package team.unnamed.inject;

import team.unnamed.inject.impl.BinderImpl;
import team.unnamed.inject.impl.InjectorImpl;
import team.unnamed.inject.key.TypeReference;

import java.util.Arrays;

public interface Injector {

    static Injector create(Module... modules) {
        return create(Arrays.asList(modules));
    }

    static Injector create(Iterable<? extends Module> modules) {
        BinderImpl binder = new BinderImpl();
        binder.install(modules);
        if (binder.hasErrors()) {
            binder.reportAttachedErrors();
        }
        return new InjectorImpl(binder);
    }

    /**
     * Returns the explicit bound provider for the specified key
     *
     * @param key The key class
     * @param <T> The key type
     * @return The provider, or null if there's no provider
     */
    default <T> Provider<? extends T> getProvider(Class<T> key) {
        return getProvider(TypeReference.of(key));
    }

    /**
     * Returns the explicit bound provider for the specified key
     *
     * @param key The key type reference
     * @param <T> The key type
     * @return The provider, or null if there's no provider
     */
    <T> Provider<? extends T> getProvider(TypeReference<T> key);

    /**
     * Injects the static members of the specified class,
     * equivalent to execute the overloaded method
     * {@link Injector#injectMembers(TypeReference, Object)}
     * passing a null instance and the type reference
     * created with the provided class
     *
     * @param clazz The class
     */
    void injectStaticMembers(Class<?> clazz);

    default void injectMembers(Object object) {
        injectMembers(TypeReference.of(object.getClass()), object);
    }

    /**
     * Lowest Level function of {@link Injector}, resolves the specified type
     * and to injects fields and methods in
     * the specified instance, if the instance is null, the injector handles
     * it like a static injection
     *
     * @param type     The injected types
     * @param instance The object that will be injected,
     *                 if it's null, the injector handles
     *                 it like a static injection
     * @param <T>      The type of the injected object
     */
    <T> void injectMembers(TypeReference<T> type, T instance);

    /**
     * Converts the specified class to a {@link TypeReference}
     * and calls the overloaded method {@link Injector#getInstance(TypeReference)}
     *
     * @param type The instance class
     * @param <T>  The class parameter
     * @return The instance, or null if the specified class
     * isn't injectable
     */
    default <T> T getInstance(Class<T> type) {
        return getInstance(TypeReference.of(type));
    }

    /**
     * Lowest-level method of {@link Injector} for instantiating
     * and injecting a class.
     *
     * @param type The instance generic type
     * @param <T>  The type parameter
     * @return The instance, or null if the class isn't injectable
     */
    <T> T getInstance(TypeReference<T> type);

}
