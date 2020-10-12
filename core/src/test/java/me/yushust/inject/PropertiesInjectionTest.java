package me.yushust.inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.Properties;

public class PropertiesInjectionTest {

  @Test
  public void test() {

    Properties properties = new Properties();

    properties.put("hello", "world");
    properties.put("world", "hello");
    properties.put("useSomeSpecialOption", false);

    Injector injector = Injector.create(binder -> {
      binder.bind(Properties.class).toInstance(properties);
      binder.bind(PropertyHolder.class).to(PropertiesHolder.class);
    });

    Baz baz = injector.getInstance(Baz.class);

    Assertions.assertEquals("world", baz.world);
    Assertions.assertEquals("hello", baz.hello);
    Assertions.assertFalse(baz.option);
  }

  public static class Baz {
    @Inject @Property("hello") private Object world;
    @Inject @Property("world") private String hello;
    @Inject @Property("useSomeSpecialOption") private boolean option;
  }

}
