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

public class InjectAllTest {

    @Test
    public void testInjectAll() {
        Injector injector = Injector.create(binder -> {
            binder
                    .bind(String.class)
                    .named("cola")
                    .toInstance("Cola.");
            binder
                    .bind(int.class)
                    .named("one")
                    .toInstance(1);
        });
        Foo foo = injector.getInstance(Foo.class);
        Assertions.assertEquals(foo.cola, "Cola.");
        Assertions.assertEquals(foo.one, 1);
        System.out.println(foo.cola);
        System.out.println(foo.one);
    }

    @InjectAll
    public static class Foo {

        @Named("cola")
        String cola;
        @Named("one")
        int one;
        @InjectIgnore
        Object object;

    }

}
