package me.yushust.inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.inject.Provider;

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
