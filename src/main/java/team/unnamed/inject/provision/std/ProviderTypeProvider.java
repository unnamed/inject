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

import team.unnamed.inject.Injector;
import team.unnamed.inject.Provider;
import team.unnamed.inject.impl.InjectorImpl;
import team.unnamed.inject.impl.ProvisionStack;
import team.unnamed.inject.key.TypeReference;
import team.unnamed.inject.provision.StdProvider;

/**
 * Represents a provider that gets instantiated the first
 * time that gets called, then the instance is saved.
 * The get() method functionality is delegated to the
 * delegate instance.
 *
 * <p>For example</p>
 * <sub>
 * public interface Foo {
 * // ...
 * }
 * public class FooProvider implements Provider&#60;Foo&#62; {
 * <p>
 * &#64;Inject private Baz baz;
 * <p>
 * &#64;Override
 * public Foo get() {
 * // create Foo using some baz property
 * }
 * }
 * </sub>
 * <p>
 * Bindings like
 * {@code bind(Foo.class).toProvider(FooProvider.class)} and
 * {@code bind(Foo.class).toProvider(new FooProvider())} works
 * <p>
 * The difference is that the binding to the provider class
 * creates the provider instance using {@link Injector#getInstance},
 * so the constructor can be injected
 */
public class ProviderTypeProvider<T>
        extends StdProvider<T>
        implements Provider<T> {

    private final TypeReference<? extends Provider<? extends T>> providerClass;
    private volatile Provider<? extends T> provider;

    public ProviderTypeProvider(TypeReference<? extends Provider<? extends T>> providerClass) {
        this.providerClass = providerClass;
    }

    @Override
    public void inject(ProvisionStack stack, InjectorImpl injector) {
        provider = injector.getInstance(providerClass);
        injected = true;
    }

    @Override
    public T get() {
        return provider.get();
    }

    /**
     * @return The targeted provider instance
     */
    public Provider<? extends T> getProvider() {
        return provider;
    }

    @Override
    public String toString() {
        return "ClassProvider(" + providerClass + ")";
    }

}
