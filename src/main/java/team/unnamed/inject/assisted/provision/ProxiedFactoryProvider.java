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

import team.unnamed.inject.assisted.ValueFactory;
import team.unnamed.inject.impl.InjectorImpl;
import team.unnamed.inject.impl.ProvisionStack;
import team.unnamed.inject.key.InjectedKey;
import team.unnamed.inject.key.Key;
import team.unnamed.inject.key.TypeReference;
import team.unnamed.inject.provision.StdProvider;
import team.unnamed.inject.resolve.solution.InjectableConstructor;
import team.unnamed.inject.util.ElementFormatter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProxiedFactoryProvider<T>
        extends StdProvider<T> {

    private final Class<? extends ValueFactory> factory;
    private final Method method;
    private final List<InjectedKey<?>> keys;
    private final InjectableConstructor constructor;
    private final Key<?> key;
    private T factoryInstance;

    ProxiedFactoryProvider(
            Class<? extends ValueFactory> factory,
            Method method,
            List<InjectedKey<?>> keys,
            InjectableConstructor constructor,
            Key<?> key
    ) {
        this.factory = factory;
        this.method = method;
        this.keys = keys;
        this.constructor = constructor;
        this.key = key;
    }

    public Class<? extends ValueFactory> getFactory() {
        return factory;
    }

    public Method getFactoryMethod() {
        return method;
    }

    public Key<?> getBuildType() {
        return key;
    }

    public Constructor<?> getTargetConstructor() {
        return constructor.getMember();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Object createInstance(InjectorImpl injector, Object[] extras) {

        Map<Key<?>, Object> values = new HashMap<>();
        for (int i = 0; i < extras.length; i++) {
            Key<?> valueKey = keys.get(i).getKey();
            Object value = extras[i];
            values.put(valueKey, value);
        }

        Object[] givenArgs = new Object[constructor.getKeys().size()];

        int i = 0;
        for (InjectedKey<?> injection : constructor.getKeys()) {
            if (injection.isAssisted()) {
                givenArgs[i] = values.get(injection.getKey());
            } else {
                Object val = injector.getInstance(
                        injector.stackForThisThread(),
                        injection.getKey(),
                        true
                );
                givenArgs[i] = val;
            }
            i++;
        }


        try {
            Object instance = constructor.getMember().newInstance(givenArgs);
            injector.injectMembers(
                    (TypeReference) key.getType(),
                    instance
            );
            return instance;
        } catch (
                InstantiationException
                        | InvocationTargetException
                        | IllegalAccessException e
        ) {
            injector.stackForThisThread().attach(
                    "Errors while invoking assisted constructor "
                            + ElementFormatter.formatConstructor(constructor.getMember(), keys),
                    e
            );
            return null;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void inject(ProvisionStack stack, InjectorImpl injector) {
        factoryInstance = (T) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[]{factory},
                (proxy, method, args) -> {
                    if (method.equals(this.method)) {
                        return createInstance(injector, args);
                    } else {
                        switch (method.getName()) {
                            case "equals":
                                return false;
                            case "hashCode":
                                return 0;
                            case "toString":
                                return factory.getName() + " Trew-generated implementation";
                            default:
                                return null;
                        }
                    }
                }
        );
        injected = true;
    }

    @Override
    public T get() {
        return factoryInstance;
    }

}
