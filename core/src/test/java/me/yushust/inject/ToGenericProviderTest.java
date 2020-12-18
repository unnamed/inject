package me.yushust.inject;

import me.yushust.inject.key.TypeReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

public class ToGenericProviderTest {

  @Inject private Foo<String> stringFoo;
  @Inject private Foo<Integer> intFoo;
  @Inject private Foo<Double> doubleFoo;

  @Test
  public void test() {

    Injector injector = Injector.create(binder ->
      binder.bind(Foo.class).toGenericProvider(new FooGenericProvider()));

    injector.injectMembers(this);

    Assertions.assertEquals("brutal", stringFoo.give());
    Assertions.assertEquals(777, intFoo.give());
    Assertions.assertEquals(123.456, doubleFoo.give());
  }

  public static class FooGenericProvider implements GenericProvider<Foo<?>> {

    @Override
    public Foo<?> get(Class<?> rawType, TypeReference<?>[] parameters) {

      Class<?> rawParameterType = parameters[0].getRawType();
      Foo<Object> foo = null;

      if (rawParameterType == String.class) {
        foo = () -> "brutal";
      } else if (rawParameterType == Integer.class) {
        foo = () -> 777;
      } else if (rawParameterType == Double.class) {
        foo = () -> 123.456;
      }
      return foo;
    }
  }

  public interface Foo<T> {
    T give();
  }

}
