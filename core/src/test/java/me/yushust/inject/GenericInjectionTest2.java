package me.yushust.inject;

import me.yushust.inject.key.TypeReference;
import org.junit.jupiter.api.Test;

public class GenericInjectionTest2 {

  @Test
  public void test() {

    Injector injector = Injector.create(binder -> {
      binder.install(new DynamicModule<>(new TypeReference<String>() {}));
    });

    Foo<String> val = injector.getInstance(TypeReference.of(Foo.class, String.class));
    System.out.println(val);
  }

  public static class DynamicModule<T> implements Module {

    private final TypeReference<T> bound;

    public DynamicModule(TypeReference<T> bound) {
      this.bound = bound;
    }

    @Override
    public void configure(Binder binder) {

      binder.bind(
          // Okay this is bad but we support this
          TypeReference.of(Foo.class, bound)
      ).to(
          TypeReference.of(FooImpl.class, bound)
      );
    }
  }

  public interface Foo<T> {
    T giveMeThis(T obj);
  }

  public static class FooImpl<T> implements Foo<T> {
    @Override
    public T giveMeThis(T obj) {
      return obj;
    }
  }

}
