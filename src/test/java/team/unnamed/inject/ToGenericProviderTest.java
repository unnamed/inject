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

import team.unnamed.inject.key.Key;
import team.unnamed.inject.provision.std.generic.GenericProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ToGenericProviderTest {

    @Inject
    private Foo<String> stringFoo;
    @Inject
    private Foo<String> stringFoo2;

    @Inject
    private Foo<Integer> intFoo;
    @Inject
    private Foo<Integer> intFoo2;

    @Inject
    private Foo<Double> doubleFoo;
    @Inject
    private Foo<Double> doubleFoo2;

    @Test
    public void test() {

        Injector injector = Injector.create(binder ->
                binder.bind(Foo.class).toGenericProvider(new FooGenericProvider()).singleton());

        injector.injectMembers(this);

        Assertions.assertSame(stringFoo, stringFoo2);
        Assertions.assertSame(intFoo, intFoo2);
        Assertions.assertSame(doubleFoo, doubleFoo2);
    }

    public interface Foo<T> {

        T give();

    }

    public static class FooGenericProvider implements GenericProvider<Foo<?>> {

        @Override
        public Foo<?> get(Key<?> match) {

            Type parameterType = ((ParameterizedType) match.getType().getType())
                    .getActualTypeArguments()[0];
            Foo<?> foo = null;

            if (parameterType == String.class) {
                foo = new StringFoo();
            } else if (parameterType == Integer.class) {
                foo = new IntegerFoo();
            } else if (parameterType == Double.class) {
                foo = new DoubleFoo();
            }
            return foo;
        }

    }

    private static class DoubleFoo implements Foo<Double> {

        @Override
        public Double give() {
            return (double) System.nanoTime();
        }

    }

    private static class IntegerFoo implements Foo<Integer> {

        @Override
        public Integer give() {
            return (int) System.nanoTime();
        }

    }

    private static class StringFoo implements Foo<String> {

        @Override
        public String give() {
            return System.nanoTime() + "";
        }

    }

}
