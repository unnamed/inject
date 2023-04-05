package team.unnamed.inject;

import team.unnamed.inject.key.TypeReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GenericInjectionTest {

    @Test
    public void test() {

        Injector injector = Injector.create(binder ->
                binder.bind(String.class).toInstance("nefasto")
        );

        Baz<String> baz = injector.getInstance(new TypeReference<Baz<String>>() {
        });
        Assertions.assertEquals("nefasto", baz.q);
    }

    public static class Baz<T> {

        @Inject
        private T q;

    }

}
