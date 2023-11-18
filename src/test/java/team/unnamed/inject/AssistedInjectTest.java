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

import team.unnamed.inject.assisted.Assist;
import team.unnamed.inject.assisted.Assisted;
import team.unnamed.inject.assisted.ValueFactory;
import team.unnamed.inject.key.TypeReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AssistedInjectTest {

    @Test
    public void test() {

        Injector injector = Injector.create(binder -> {
            binder.bind(Foo.class).toFactory(FooFactory.class);
            binder.bind(new TypeReference<Baz<Double>>() {
                    })
                    .toFactory(new TypeReference<GenericBazFactory<Double>>() {
                    });
        });

        // first normal assisted factory
        FooFactory factory = injector.getInstance(FooFactory.class);
        Foo foo = factory.create("hello", 123);

        Assertions.assertEquals("hello", foo.name);
        Assertions.assertEquals(123, foo.number);
        Assertions.assertNotNull(foo.baz);
        Assertions.assertNotNull(foo.bar);

        // second normal assisted factory
        FooFactory factory2 = injector.getInstance(FooFactory.class);
        Foo foo2 = factory2.create("hello", 123);
        Assertions.assertEquals("hello", foo2.name);
        Assertions.assertNotNull(foo2.baz);

        // first generic assisted factory
        GenericBazFactory<Double> doubleBazFactory
                = injector.getInstance(new TypeReference<GenericBazFactory<Double>>() {
        });
        Baz<Double> doubleBaz = doubleBazFactory.create("hi", 0.1D);

        Assertions.assertNotNull(doubleBaz.bar);
        Assertions.assertNotNull(doubleBaz.bar2);
        Assertions.assertEquals("hi", doubleBaz.name);
        Assertions.assertEquals(0.1D, doubleBaz.value);
    }

    public interface GenericBazFactory<T> extends ValueFactory {

        Baz<T> create(String name, T value);

    }

    public interface FooFactory extends ValueFactory {

        Foo create(String name, int asd);

    }

    public static class Bar {

    }

    public static class Foo {

        private final String name;
        private final int number;
        private final Bar baz;
        @Inject
        private Bar bar;

        @Assisted
        public Foo(
                @Assist String name,
                @Assist int number,
                Bar baz
        ) {
            this.name = name;
            this.number = number;
            this.baz = baz;
        }

    }

    public static class Baz<T> {

        private final String name;
        private final T value;
        private final Bar bar;
        @Inject
        private Bar bar2;

        @Assisted
        public Baz(
                @Assist String name,
                @Assist T value,
                Bar bar
        ) {
            this.name = name;
            this.value = value;
            this.bar = bar;
        }

    }

}
