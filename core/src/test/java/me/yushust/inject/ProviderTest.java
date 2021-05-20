package me.yushust.inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.inject.Provider;

public class ProviderTest {

  @Inject private Abstraction abstraction;

  @Test
  public void test() {
    Injector injector = Injector.create(binder ->
      binder.bind(Abstraction.class).toProvider(TheProvider.class).singleton());
    injector.injectMembers(this);

    Assertions.assertNotNull(abstraction);
  }

  public interface Abstraction {
  }

  public interface NoImpl {
  }

  public static class Implementation implements Abstraction {
  }

  public static class Requirement {
  }

  public static class Requirement2 {
  }

  public static class Requirement3 {
  }

  public static class Requirement4 {
    @Inject private NoImpl obj;
  }

  public static class TheProvider implements Provider<Abstraction> {

    @Inject private Requirement req1;
    @Inject private Requirement2 req2;
    @Inject private Requirement3 req3;
    @Inject private Requirement4 req4;

    @Override
    public Abstraction get() {
      Assertions.assertNotNull(req1);
      Assertions.assertNotNull(req2);
      Assertions.assertNotNull(req3);
      Assertions.assertNotNull(req4);
      System.out.println(req4.obj);
      return new Implementation();
    }
  }

}
