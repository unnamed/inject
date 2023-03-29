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
