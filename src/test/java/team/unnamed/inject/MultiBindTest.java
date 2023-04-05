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
