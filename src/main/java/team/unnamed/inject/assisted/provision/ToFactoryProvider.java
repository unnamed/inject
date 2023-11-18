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
package team.unnamed.inject.assisted.provision;

import team.unnamed.inject.assisted.Assisted;
import team.unnamed.inject.assisted.ValueFactory;
import team.unnamed.inject.error.FactoryException;
import team.unnamed.inject.impl.BinderImpl;
import team.unnamed.inject.key.InjectedKey;
import team.unnamed.inject.key.Key;
import team.unnamed.inject.key.TypeReference;
import team.unnamed.inject.provision.StdProvider;
import team.unnamed.inject.resolve.ComponentResolver;
import team.unnamed.inject.resolve.solution.InjectableConstructor;
import team.unnamed.inject.util.Validate;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a provider of a factory. The keys aren't really
 * bound to this provider. The provider is never put on the
 * bindings map, instead of it, it binds the factory type to
 * a {@link ProxiedFactoryProvider}
 *
 * @param <T> The bound key type
 */
public class ToFactoryProvider<T>
        extends StdProvider<T> {

    private final TypeReference<? extends ValueFactory> factory;

    public ToFactoryProvider(TypeReference<? extends ValueFactory> factory) {
        this.factory = Validate.notNull(factory, "factory");
    }

    @Override
    public boolean onBind(BinderImpl binder, Key<?> key) {

        Class<? extends ValueFactory> factoryRawType = factory.getRawType();
        TypeReference<?> required = key.getType();
        InjectableConstructor constructor = ComponentResolver
                .constructor()
                .resolve(binder, required, Assisted.class);

        // check created object
        if (constructor == null) {
            binder.attach(
                    "Bad assisted object",
                    new FactoryException("Cannot resolve constructor annotated with @Assisted in type " + required)
            );
            return false;
        }

        // check factory class
        if (!factoryRawType.isInterface()) {
            binder.attach("Factory " + factory
                    + " must be an interface with one single method!");
            return false;
        }

        int methodCount = factoryRawType.getMethods().length;
        if (methodCount != 1) {
            binder.attach(
                    "Bad factory method",
                    new FactoryException("Factory " + factory
                            + " has invalid method count (expected: 1, found: " + methodCount + ")")
            );
            return false;
        }

        Method method = factoryRawType.getMethods()[0];
        Type methodReturnType = factory.resolve(method.getGenericReturnType());

        // check return type is equal to the bound key
        if (!required.equals(methodReturnType)) {
            binder.attach(
                    "Bad factory method",
                    new FactoryException("Method " + method.getName() + " of factory "
                            + factory + " must return " + required)
            );
            return false;
        }

        List<InjectedKey<?>> keys = ComponentResolver.keys().keysOf(
                factory,
                method.getParameters()
        );

        Set<Key<?>> assists = new HashSet<>();

        for (InjectedKey<?> parameterKey : keys) {
            if (!assists.add(parameterKey.getKey())) {
                binder.attach(
                        "Duplicated factory assisted keys",
                        new FactoryException("Creator method has two equal assisted values! " +
                                "Consider using qualifiers to difference them (key " + parameterKey.getKey() + ")")
                );
                return false;
            }
        }

        Set<Key<?>> constructorAssists = new HashSet<>();

        for (InjectedKey<?> parameterKey : constructor.getKeys()) {
            if (parameterKey.isAssisted()) {
                if (!assists.contains(parameterKey.getKey())) {
                    binder.attach(
                            "Unsatisfied Assisted Constructor",
                            new FactoryException("Constructor requires assist for "
                                    + parameterKey.getKey() + " and method doesn't give it!")
                    );
                    return false;
                } else if (!constructorAssists.add(parameterKey.getKey())) {
                    binder.attach(
                            "Duplicated constructor assisted keys",
                            new FactoryException("Constructor has two equal assisted keys! " +
                                    "Consider using qualifiers to difference them (key " + parameterKey.getKey() + ")")
                    );
                    return false;
                }
            }
        }

        if (assists.size() != constructorAssists.size()) {
            binder.attach(
                    "Assists mismatch, different assisted injections count",
                    new FactoryException("Assists mismatch! Constructor has "
                            + constructorAssists.size() + " values and method " + assists.size() + " values.")
            );
            return false;
        }

        @SuppressWarnings("unchecked")
        Key<T> castedKey = (Key<T>) key;
        binder.$unsafeBind(Key.of(factory), new ProxiedFactoryProvider<>(
                factoryRawType,
                method,
                keys,
                constructor,
                castedKey
        ));
        return false;
    }

    @Override
    public T get() {
        throw new IllegalStateException("The instance is bound to a Factory, you must get an instance of that factory!");
    }

}
