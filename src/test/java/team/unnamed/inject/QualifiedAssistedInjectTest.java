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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class QualifiedAssistedInjectTest {

    @Test
    public void test() {

        Injector injector = Injector.create(binder ->
                binder.bind(Foo.class).toFactory(FooFactory.class));

        FooFactory factory = injector.getInstance(FooFactory.class);
        Foo foo = factory.create("OcNo", "Miranda", "Mr.");

        Assertions.assertEquals("Mr. OcNo Miranda", foo.identifier);
        Assertions.assertNotNull(foo.bar);
        Assertions.assertNotNull(foo.bar2);
    }

    public interface FooFactory extends ValueFactory {

        Foo create(@Named("name") String name, @Named("lastName") String lastName, String prefix);

    }

    public static class Bar {

    }

    public static class Foo {

        private final String identifier;
        private final Bar bar2;
        @Inject
        private Bar bar;

        @Assisted
        public Foo(
                @Assist @Named("name") String name,
                @Assist @Named("lastName") String lastName,
                @Assist String prefix,
                Bar bar2
        ) {
            this.bar2 = bar2;
            this.identifier = prefix + " " + name + " " + lastName;
        }

    }

}
