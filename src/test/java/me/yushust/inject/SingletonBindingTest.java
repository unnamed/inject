package me.yushust.inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class SingletonBindingTest {

  @Test
  public void test() {

    Injector injector = Injector.create(binder ->
        binder.bind(UUID.class)
            .toProvider(UUID::randomUUID)
            .singleton()
    );

    UUID id = injector.getInstance(UUID.class);
    UUID id2 = injector.getInstance(UUID.class);

    Assertions.assertSame(id, id2);
  }

}
