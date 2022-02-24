package me.yushust.inject.explore;

import me.yushust.inject.Injector;
import me.yushust.inject.assisted.Assist;
import me.yushust.inject.assisted.Assisted;
import me.yushust.inject.assisted.ValueFactory;
import me.yushust.inject.assisted.provision.ProxiedFactoryProvider;
import me.yushust.inject.key.Key;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Provider;

public class ProxiedFactoryProviderExploreTest {

    @Test
    public void test() {

        Injector injector = Injector.create(binder -> binder.bind(Foo.class).toFactory(FooFactory.class));
        Provider<? extends FooFactory> provider = injector.getProvider(FooFactory.class);
        ProxiedFactoryProvider<?> proxiedProvider = (ProxiedFactoryProvider<?>) provider;

        Assertions.assertEquals(Key.of(Foo.class), proxiedProvider.getBuildType());
        Assertions.assertEquals(FooFactory.class, proxiedProvider.getFactory());
        Assertions.assertEquals("create", proxiedProvider.getFactoryMethod().getName());
    }

    public interface FooFactory extends ValueFactory {

        Foo create(String name);

    }

    public static class Foo {

        @Assisted
        public Foo(
                @Assist String name,
                Object empty
        ) {
        }

    }

}
