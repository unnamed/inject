package me.yushust.inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.inject.Named;

public class ProviderMethodsTest {

  public static class MyModule extends AbstractModule {
    @Provides
    public String provideName() {
      return "trew";
    }

    @Provides @Named("hello")
    public String provideHello() {
      return "hello";
    }
  }

  @Test
  public void test() {
    Injector injector = Injector.create(new MyModule());
    Foo foo = injector.getInstance(Foo.class);
    Assertions.assertEquals("trew", foo.trew);
    Assertions.assertEquals("hello", foo.hello);
  }


  public static class Foo {
    @Inject private String trew;
    @Inject @Named("hello") private String hello;
  }

}
