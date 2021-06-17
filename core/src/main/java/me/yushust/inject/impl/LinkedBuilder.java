package me.yushust.inject.impl;

import me.yushust.inject.Binder;
import me.yushust.inject.assisted.ValueFactory;
import me.yushust.inject.assisted.provision.ToFactoryProvider;
import me.yushust.inject.key.Key;
import me.yushust.inject.key.TypeReference;
import me.yushust.inject.provision.Providers;
import me.yushust.inject.provision.std.generic.GenericProvider;
import me.yushust.inject.provision.std.generic.ToGenericProvider;
import me.yushust.inject.util.Validate;

import javax.inject.Provider;

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
