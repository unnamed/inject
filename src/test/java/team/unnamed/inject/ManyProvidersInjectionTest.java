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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ManyProvidersInjectionTest {

    @Inject
    private Blah blah;
    @Inject
    private Baz baz;
    @Inject
    private Bar bar;
    @Inject
    private Foo foo;

    @Test
    public void test() {

        Injector injector = Injector.create(binder -> {
            binder.bind(Baz.class).toProvider(BazProvider.class);
            binder.bind(Bar.class).toProvider(BarProvider.class);
            binder.bind(Foo.class).toProvider(FooProvider.class);
        });
        injector.injectMembers(this);

        Assertions.assertNotNull(bar);
        Assertions.assertNotNull(baz);
        Assertions.assertNotNull(bar);
        Assertions.assertNotNull(foo);

        blah.checkDepends();
        baz.checkDepends();
        bar.checkDepends();
        foo.checkDepends();
    }

    public interface Baz {

        Object[] get();

        default void checkDepends() {
            Object[] vals = get();
            Assertions.assertEquals(2, vals.length);

            Object blah = vals[0];
            Object bar = vals[1];

            Assertions.assertTrue(blah instanceof Blah, "expected instance of Blah");
            Assertions.assertTrue(bar instanceof Bar, "expected instance of Bar");

            ((Blah) blah).checkDepends();
            ((Bar) bar).checkDepends();
        }

    }

    public interface Bar {

        Blah get();

        default void checkDepends() {
            Assertions.assertNotNull(get());
        }

    }

    public interface Foo {

        Object[] get();

        default void checkDepends() {
            Object[] vals = get();
            Assertions.assertEquals(3, vals.length);

            Object blah = vals[0];
            Object baz = vals[1];
            Object bar = vals[2];

            Assertions.assertTrue(blah instanceof Blah, "expected instance of Blah");
            Assertions.assertTrue(baz instanceof Baz, "expected instance of Baz");
            Assertions.assertTrue(bar instanceof Bar, "expected instance of Bar");

            ((Blah) blah).checkDepends();
            ((Baz) baz).checkDepends();
            ((Bar) bar).checkDepends();
        }

    }

    public static class Blah {

        @Inject
        private Baz baz;
        @Inject
        private Bar bar;

        void checkDepends() {
            Assertions.assertNotNull(baz);
            Assertions.assertNotNull(bar);
        }

    }

    public static class BarProvider implements Provider<Bar> {

        @Inject
        private Blah blah;

        @Override
        public Bar get() {
            return () -> blah;
        }

    }

    public static class BazProvider implements Provider<Baz> {

        @Inject
        private Blah blah;
        @Inject
        private Bar bar;

        @Override
        public Baz get() {
            return () -> new Object[]{blah, bar};
        }

    }

    public static class FooProvider implements Provider<Foo> {

        @Inject
        private Blah blah;
        @Inject
        private Baz baz;
        @Inject
        private Bar bar;

        @Override
        public Foo get() {
            return () -> new Object[]{blah, baz, bar};
        }

    }

}
