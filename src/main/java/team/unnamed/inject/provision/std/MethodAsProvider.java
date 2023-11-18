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
package team.unnamed.inject.provision.std;

import team.unnamed.inject.Provider;
import team.unnamed.inject.Provides;
import team.unnamed.inject.error.BindingException;
import team.unnamed.inject.error.ErrorAttachable;
import team.unnamed.inject.impl.InjectorImpl;
import team.unnamed.inject.impl.ProvisionStack;
import team.unnamed.inject.key.Key;
import team.unnamed.inject.key.TypeReference;
import team.unnamed.inject.provision.StdProvider;
import team.unnamed.inject.resolve.ComponentResolver;
import team.unnamed.inject.resolve.solution.InjectableMethod;
import team.unnamed.inject.scope.Scope;
import team.unnamed.inject.scope.Scopes;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a binding to a method, the method is used like a provider,
 * passing the dependencies as parameters and getting an instance with
 * the return value
 */
public class MethodAsProvider<T>
        extends StdProvider<T> {

    private final Object moduleInstance;
    private final InjectableMethod method;
    private InjectorImpl injector;

    public MethodAsProvider(Object moduleInstance, InjectableMethod method) {
        this.moduleInstance = moduleInstance;
        this.method = method;
    }

    public static <T> Map<Key<?>, Provider<?>> resolveMethodProviders(
            ErrorAttachable errors,
            TypeReference<T> type,
            T instance
    ) {

        Map<Key<?>, Provider<?>> providers = new HashMap<>();

        for (InjectableMethod injectableMethod : ComponentResolver.methods().resolve(type, Provides.class)) {
            Method method = injectableMethod.getMember();
            // TODO: Replace this shit
            Key<?> key = ComponentResolver.keys().keyOf(
                    injectableMethod.getDeclaringType().resolve(method.getGenericReturnType()),
                    method.getAnnotations()
            ).getKey();

            Scope scope = Scopes.getScanner().scan(method);

            Provider<?> provider = new MethodAsProvider<>(instance, injectableMethod)
                    .withScope(key, scope);

            if (providers.putIfAbsent(key, provider) != null) {
                errors.attach(
                        "Method provider duplicate",
                        new BindingException("Type " + type + " has two or more method " +
                                "providers with the same return key!")
                );
            }
        }

        return providers;
    }

    @Override
    public void inject(ProvisionStack stack, InjectorImpl injector) {
        this.injector = injector;
        this.injected = true;
    }

    @Override
    public T get() {
        @SuppressWarnings("unchecked")
        T value = (T) method.inject(injector, injector.stackForThisThread(), moduleInstance);
        return value;
    }

}
