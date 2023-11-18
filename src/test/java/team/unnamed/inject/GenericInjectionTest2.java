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

import team.unnamed.inject.key.TypeReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class GenericInjectionTest2 {

    @Test
    public void test() {

        Injector injector = Injector.create(binder ->
                binder.install(new DynamicModule<>(new TypeReference<String>() {
                })));

        TypeReference<List<String>> expected = new TypeReference<List<String>>() {
        };
        TypeReference<List<String>> type = injector.getInstance(new TypeReference<TypeReference<List<String>>>() {
        });

        Assertions.assertEquals(expected, type);

        Foo<String> val = injector.getInstance(TypeReference.of(Foo.class, String.class));
        Assertions.assertNotNull(val);
    }

    public interface Foo<T> {

    }

    public static class DynamicModule<T> implements Module {

        private final TypeReference<T> bound;

        public DynamicModule(TypeReference<T> bound) {
            this.bound = bound;
        }

        @Override
        public void configure(Binder binder) {

            binder.bind(
                    // Okay this is bad but we support this
                    TypeReference.of(Foo.class, bound)
            ).to(
                    TypeReference.of(FooImpl.class, bound)
            );
        }

    }

    public static class FooImpl<T> implements Foo<T> {

    }

}
