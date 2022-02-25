package team.unnamed.inject.explore;

import team.unnamed.inject.Injector;
import team.unnamed.inject.assisted.Assist;
import team.unnamed.inject.assisted.Assisted;
import team.unnamed.inject.assisted.ValueFactory;
import team.unnamed.inject.assisted.provision.ProxiedFactoryProvider;
import team.unnamed.inject.key.Key;
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
