package team.unnamed.inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.inject.Named;

public class QualifiedBindingTest {

    @Test
    public void test() {

        Injector injector = Injector.create(binder ->
                binder.bind(String.class)
                        .named("hello")
                        .toInstance("world")
        );

        Foo foo = injector.getInstance(Foo.class);
        Assertions.assertEquals("", foo.hello);
        Assertions.assertEquals("world", foo.world);

    }

    public static class Foo {

        // this should create an empty string
        @Inject
        private String hello;
        @Inject
        @Named("hello")
        private String world;

    }

}
