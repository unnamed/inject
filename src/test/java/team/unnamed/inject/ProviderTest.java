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

import team.unnamed.inject.error.InjectionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ProviderTest {

    @Inject
    private Abstraction abstraction;

    @Test
    public void test() {
        Injector injector = Injector.create(binder ->
                binder.bind(Abstraction.class).toProvider(TheProvider.class).singleton());
        Assertions.assertThrows(InjectionException.class, () ->
                injector.injectMembers(this));
    }

    public interface Abstraction {

    }

    public interface NoImpl {

    }

    public static class Implementation implements Abstraction {

    }

    public static class Requirement {

    }

    public static class Requirement2 {

    }

    public static class Requirement3 {

    }

    public static class Requirement4 {

        @Inject
        private NoImpl obj;

    }

    public static class TheProvider implements Provider<Abstraction> {

        @Inject
        private Requirement req1;
        @Inject
        private Requirement2 req2;
        @Inject
        private Requirement3 req3;
        @Inject
        private Requirement4 req4;

        @Override
        public Abstraction get() {
            Assertions.assertNotNull(req1);
            Assertions.assertNotNull(req2);
            Assertions.assertNotNull(req3);
            Assertions.assertNotNull(req4);
            return new Implementation();
        }

    }

}
