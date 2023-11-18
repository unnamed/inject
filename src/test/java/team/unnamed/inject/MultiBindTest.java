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

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MultiBindTest {

    @Test
    public void test() {

        Injector injector = Injector.create(binder -> {

            binder.multibind(String.class)
                    .asList()
                    .toInstance("hello")
                    .toInstance("world");

            binder.multibind(UUID.class)
                    .asList()
                    .toProvider(UUID::randomUUID)
                    .singleton();

            binder.multibind(UUID.class)
                    .asList()
                    .toProvider(UUID::randomUUID);

            binder.multibind(Integer.class)
                    .asMap(String.class)
                    .bind("one").toInstance(1)
                    .bind("two").toInstance(2);

            binder.multibind(UUID.class)
                    .named("1")
                    .asMap(String.class)
                    .bind("Yusshu").toInstance(UUID.randomUUID());
        });

        Baz baz = injector.getInstance(Baz.class);
        Assertions.assertEquals(2, baz.stringList.size());
        Assertions.assertEquals("hello", baz.stringList.get(0));
        Assertions.assertEquals("world", baz.stringList.get(1));

        Assertions.assertEquals(2, baz.ids1.size());
        Assertions.assertEquals(baz.ids1, baz.ids2);

        Assertions.assertEquals(2, baz.numbersByName.size());
        Assertions.assertEquals(1, baz.numbersByName.get("one"));
        Assertions.assertEquals(2, baz.numbersByName.get("two"));

        Assertions.assertNotNull(baz.map);
    }

    public static class Baz {

        @Inject
        private Map<String, Integer> numbersByName;
        @Inject
        private List<String> stringList;
        @Inject
        private List<UUID> ids1;
        @Inject
        private List<UUID> ids2;

        @Inject @Named("1") private Map<String, UUID> map;

    }

}
