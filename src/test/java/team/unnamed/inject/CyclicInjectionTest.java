package team.unnamed.inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

public class CyclicInjectionTest {

    @Test
    public void test() {

        Injector injector = Injector.create();
        Foo foo = injector.getInstance(Foo.class);

        Assertions.assertNotNull(foo);
        Assertions.assertNotNull(foo.bar);
        Assertions.assertNotNull(foo.bar.foo);
        Assertions.assertNotNull(foo.bar.foo.bar);
    }

    public static class Foo {

        @Inject
        private Bar bar;

        @Inject
        public void inject() {
            Assertions.assertNotNull(bar);
        }

    }

    public static class Bar {

        @Inject
        private Foo foo;

        @Inject
        public void inject() {
            Assertions.assertNotNull(foo);
        }

    }

}
