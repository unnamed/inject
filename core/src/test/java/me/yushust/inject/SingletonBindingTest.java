package me.yushust.inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.UUID;

public class SingletonBindingTest {

  @Inject private UUID id;
  @Inject private UUID id2;
  @Inject private UUID id3;

  @Test
  public void test() {

    Injector injector = Injector.create(binder ->
        binder.bind(UUID.class)
            .toProvider(UUID::randomUUID)
            .singleton()
    );

    injector.injectMembers(this);

    Assertions.assertSame(id, id2);
    Assertions.assertSame(id3, id2);
    Assertions.assertSame(id, injector.getInstance(UUID.class));
  }

}
