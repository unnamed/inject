package me.yushust.inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

public class CyclicInjectionTest {

  @Test
  public void test() {

    Injector injector = InjectorFactory.create();
    Foo foo = injector.getInstance(Foo.class);

    Assertions.assertNotNull(foo);
    Assertions.assertNotNull(foo.bar);
    Assertions.assertNotNull(foo.bar.foo);
    Assertions.assertNotNull(foo.bar.foo.bar);
  }

  public static class Foo {
    @Inject private Bar bar;

    @Inject
    private void inject() {
      Assertions.assertNotNull(bar);
      Assertions.assertNotNull(bar.foo);
      Assertions.assertNotNull(bar.foo.bar);
      Assertions.assertNotNull(bar.foo.bar.foo);
    }
  }

  public static class Bar {
    @Inject private Foo foo;

    @Inject
    private void inject() {
      Assertions.assertNotNull(foo);
      Assertions.assertNotNull(foo.bar);
      Assertions.assertNotNull(foo.bar.foo);
      Assertions.assertNotNull(foo.bar.foo.bar);
    }
  }

}
