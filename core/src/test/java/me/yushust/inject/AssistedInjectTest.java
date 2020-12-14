package me.yushust.inject;

import me.yushust.inject.assisted.Assist;
import me.yushust.inject.assisted.ValueFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

public class AssistedInjectTest {

  @Test
  public void test() {

    Injector injector = Injector.create(binder -> {
      binder.bind(String.class).toInstance("hello");
      binder.bind(Foo.class).toFactory(FooFactory.class);
    });

    FooFactory factory = injector.getInstance(FooFactory.class);
    Foo foo = factory.create("hello");

    Assertions.assertEquals("hello", foo.name);
    Assertions.assertNotNull(foo.baz);
    Assertions.assertNotNull(foo.bar);
  }

  public static class Bar {
  }

  public static class Foo {

    private final String name;
    private final Bar baz;
    @Inject private Bar bar;

    @Inject
    public Foo(
        @Assist String name,
        Bar baz
    ) {
      this.name = name;
      this.baz = baz;
    }

  }

  public interface FooFactory extends ValueFactory {

    Foo create(String name);

  }

}
