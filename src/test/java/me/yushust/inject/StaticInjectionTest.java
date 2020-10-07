package me.yushust.inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

public class StaticInjectionTest {

  @Test
  public void test() {
    Injector injector = Injector.create(binder ->
      binder.bind(String.class).toInstance("use trew")
    );
    Bar bar = injector.getInstance(Bar.class);

    Assertions.assertEquals("use trew", bar.empty);
    Assertions.assertNull(Bar.staticEmpty); // it should not be injected in a instance-injection

    injector.injectStaticMembers(Bar.class);
    Assertions.assertEquals("use trew", Bar.staticEmpty);
  }

  public static class Bar {
    @Inject private static String staticEmpty;
    @Inject private String empty;
  }

}
