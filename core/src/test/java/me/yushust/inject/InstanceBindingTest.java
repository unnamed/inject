package me.yushust.inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

public class InstanceBindingTest {

  @Test
  public void test() {

    Baz baz = new Baz(); // not injected, the injector doesn't know about this instance

    Injector injector = Injector.create(binder ->
        binder.bind(Baz.class).toInstance(baz)
    );
    Baz baz2 = injector.getInstance(Baz.class);

    Assertions.assertSame(baz, baz2);
  }

  public static class Baz {
  }

}
