package me.yushust.inject.impl;

import me.yushust.inject.key.Key;
import me.yushust.inject.provision.Providers;
import me.yushust.inject.provision.StdProvider;
import me.yushust.inject.provision.std.ScopedProvider;
import me.yushust.inject.provision.std.generic.ToGenericProvider;

public class ProvisionHandle {

	private final InjectorImpl injector;
	private final BinderImpl binder;

	public ProvisionHandle(
			InjectorImpl injector,
			BinderImpl binder
	) {
		this.injector = injector;
		this.binder = binder;
	}

	private <T> StdProvider<T> getGenericProvider(Class<T> rawType, Key<T> match) {

		Key<T> rawTypeKey = Key.of(rawType);

		StdProvider<T> provider = binder.getProvider(rawTypeKey);

		if (provider instanceof ScopedProvider) {
			ScopedProvider<T> scopedProvider = (ScopedProvider<T>) provider;
			if (scopedProvider.requiresJitScoping()) {
				provider = (StdProvider<T>) scopedProvider
						.withScope(match, scopedProvider.getScope());
				binder.$unsafeBind(match, provider);
			}
		}

		if (!(provider instanceof ToGenericProvider.SyntheticGenericProvider)) {
			return null;
		} else {
			return provider;
		}
	}

	public <T> StdProvider<T> getProviderAndInject(ProvisionStack stack, Key<T> key) {
		StdProvider<T> provider = binder.getProvider(key);
		if (provider == null) {
			Class<T> rawType = key.getType().getRawType();
			if (key.getType().getType() != rawType) {
				if ((provider = getGenericProvider(rawType, key)) == null) {
					return null;
				}
			} else {
				return null;
			}
		}
		if (!provider.isInjected()) {
			Providers.inject(stack, injector, provider);
		}
		return provider;
	}

}
