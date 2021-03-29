package me.yushust.inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

public class ConstructorInjectionTest {

  @Test
  public void test() {
    Injector injector = Injector.create(binder -> {
      binder.bind(Bar.class).toInstance(new Bar());
    });

    Foo foo = injector.getInstance(Foo.class);
    Assertions.assertNotNull(foo);
  }

  public static class Bar {
  }

  public static class Foo {

    @Inject
    public Foo(Bar bar) {
      Assertions.assertNotNull(bar);
    }
  }

}
