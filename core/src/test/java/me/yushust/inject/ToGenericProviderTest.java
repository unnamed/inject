package me.yushust.inject;

import me.yushust.inject.key.TypeReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

public class ToGenericProviderTest {

  @Inject private Foo<String> stringFoo;
  @Inject private Foo<String> stringFoo2;

  @Inject private Foo<Integer> intFoo;
  @Inject private Foo<Integer> intFoo2;

  @Inject private Foo<Double> doubleFoo;
  @Inject private Foo<Double> doubleFoo2;

  @Test
  public void test() {

    Injector injector = Injector.create(binder ->
      binder.bind(Foo.class).toGenericProvider(new FooGenericProvider()).singleton());

    injector.injectMembers(this);

    Assertions.assertSame(stringFoo, stringFoo2);
    Assertions.assertSame(intFoo, intFoo2);
    Assertions.assertSame(doubleFoo, doubleFoo2);
  }

  public static class FooGenericProvider implements GenericProvider<Foo<?>> {

    @Override
    public Foo<?> get(Class<?> rawType, TypeReference<?>[] parameters) {

      Class<?> rawParameterType = parameters[0].getRawType();
      Foo<?> foo = null;

      if (rawParameterType == String.class) {
        foo = new StringFoo();
      } else if (rawParameterType == Integer.class) {
        foo = new IntegerFoo();
      } else if (rawParameterType == Double.class) {
        foo = new DoubleFoo();
      }
      return foo;
    }
  }

  private static class DoubleFoo implements Foo<Double> {
    @Override
    public Double give() {
      return (double) System.nanoTime();
    }
  }

  private static class IntegerFoo implements Foo<Integer> {
    @Override
    public Integer give() {
      return (int) System.nanoTime();
    }
  }

  private static class StringFoo implements Foo<String> {
    @Override
    public String give() {
      return System.nanoTime() + "";
    }
  }

  public interface Foo<T> {
    T give();
  }

}
