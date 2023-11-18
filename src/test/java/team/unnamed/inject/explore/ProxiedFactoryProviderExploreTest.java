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
package team.unnamed.inject.explore;

import team.unnamed.inject.Injector;
import team.unnamed.inject.Provider;
import team.unnamed.inject.assisted.Assist;
import team.unnamed.inject.assisted.Assisted;
import team.unnamed.inject.assisted.ValueFactory;
import team.unnamed.inject.assisted.provision.ProxiedFactoryProvider;
import team.unnamed.inject.key.Key;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ProxiedFactoryProviderExploreTest {

    @Test
    public void test() {

        Injector injector = Injector.create(binder -> binder.bind(Foo.class).toFactory(FooFactory.class));
        Provider<? extends FooFactory> provider = injector.getProvider(FooFactory.class);
        ProxiedFactoryProvider<?> proxiedProvider = (ProxiedFactoryProvider<?>) provider;

        Assertions.assertEquals(Key.of(Foo.class), proxiedProvider.getBuildType());
        Assertions.assertEquals(FooFactory.class, proxiedProvider.getFactory());
        Assertions.assertEquals("create", proxiedProvider.getFactoryMethod().getName());
    }

    public interface FooFactory extends ValueFactory {

        Foo create(String name);

    }

    public static class Foo {

        @Assisted
        public Foo(
                @Assist String name,
                Object empty
        ) {
        }

    }

}
