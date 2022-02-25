package team.unnamed.inject.impl;

import team.unnamed.inject.key.Key;
import team.unnamed.inject.provision.Providers;
import team.unnamed.inject.provision.StdProvider;
import team.unnamed.inject.provision.std.ScopedProvider;
import team.unnamed.inject.provision.std.generic.ToGenericProvider;

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
