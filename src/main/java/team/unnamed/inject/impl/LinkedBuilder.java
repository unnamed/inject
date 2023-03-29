package team.unnamed.inject.impl;

import team.unnamed.inject.Binder;
import team.unnamed.inject.Provider;
import team.unnamed.inject.assisted.ValueFactory;
import team.unnamed.inject.assisted.provision.ToFactoryProvider;
import team.unnamed.inject.key.Key;
import team.unnamed.inject.key.TypeReference;
import team.unnamed.inject.provision.Providers;
import team.unnamed.inject.provision.std.generic.GenericProvider;
import team.unnamed.inject.provision.std.generic.ToGenericProvider;
import team.unnamed.inject.util.Validate;

public interface LinkedBuilder<R, T> extends Binder.Linked<R, T> {

    Key<T> key();

    @Override
    default R toGenericProvider(GenericProvider<? extends T> provider) {
        Validate.notNull(provider, "provider");
        return toProvider(new ToGenericProvider<>(provider));
    }

    @Override
    default void toFactory(TypeReference<? extends ValueFactory> factory) {
        Validate.notNull(factory, "factory");
        toProvider(new ToFactoryProvider<>(factory));
    }

    @Override
    default R to(TypeReference<? extends T> targetType) {
        Validate.notNull(targetType, "targetType");
        return toProvider(Providers.link(key(), Key.of(targetType)));
    }

    @Override
    default <P extends Provider<? extends T>> R toProvider(TypeReference<P> providerClass) {
        Validate.notNull(providerClass, "providerClass");
        return toProvider(Providers.providerTypeProvider(providerClass));
    }

}
