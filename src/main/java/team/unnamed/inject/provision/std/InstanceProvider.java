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
import team.unnamed.inject.key.Key;
import team.unnamed.inject.provision.StdProvider;
import team.unnamed.inject.scope.Scope;
import team.unnamed.inject.scope.Scopes;

/**
 * Represents an instance binding. The key is bound to a
 * specific instance. For example:
 *
 * <pre>
 *   public class Foo {
 *
 *     private final String name;
 *     // ...
 *   }
 * </pre>
 *
 * <p>The binding:
 * {@code bind(Foo.class).toInstance(new Foo());}
 * should work</p>
 * <p>
 * The bound instances are not injected
 */
public class InstanceProvider<T>
        extends StdProvider<T>
        implements Provider<T> {

    private final T instance;

    public InstanceProvider(T instance) {
        this.instance = instance;
        setInjected(true);
    }

    @Override
    public Provider<T> withScope(Key<?> match, Scope scope) {
        if (scope == Scopes.SINGLETON) {
            return this;
        } else {
            throw new UnsupportedOperationException("Instance providers cannot be scoped!");
        }
    }

    @Override
    public T get() {
        return instance;
    }

    @Override
    public String toString() {
        return "instance '" + instance + "'";
    }

}
