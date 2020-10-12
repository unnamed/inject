package me.yushust.inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LinkedBindingTest {

  @Test
  public void test() {
    Injector injector = Injector.create(binder ->
        binder.bind(Foo.class).to(FooImpl.class)
    );
    Foo foo = injector.getInstance(Foo.class);
    Assertions.assertTrue(foo instanceof FooImpl);
  }

  public interface Foo {

  }

  public static class FooImpl implements Foo {

  }

}
