package team.unnamed.inject.provision;

import team.unnamed.inject.impl.InjectorImpl;
import team.unnamed.inject.impl.ProvisionStack;
import team.unnamed.inject.key.Key;
import team.unnamed.inject.key.TypeReference;
import team.unnamed.inject.provision.std.InstanceProvider;
import team.unnamed.inject.provision.std.LinkedProvider;
import team.unnamed.inject.provision.std.ProviderTypeProvider;
import team.unnamed.inject.provision.std.ScopedProvider;
import team.unnamed.inject.util.Validate;

import javax.inject.Provider;

/**
 * Collection of static factory methods to create providers
 */
public final class Providers {

    private Providers() {
    }

    public static void inject(ProvisionStack stack, InjectorImpl injector, Provider<?> provider) {
        if (provider instanceof StdProvider) {
            ((StdProvider<?>) provider).inject(stack, injector);
        } else {
            injector.injectMembers(stack, Key.of(TypeReference.of(provider.getClass())), provider);
        }
    }

    public static <T> Provider<T> unwrap(Provider<T> provider) {
        if (provider instanceof DelegatingStdProvider) {
            return unwrap(((DelegatingStdProvider<T>) provider).getDelegate());
        } else if (provider instanceof ScopedProvider) {
            return unwrap(((ScopedProvider<T>) provider).getUnscoped());
        } else {
            return provider;
        }
    }

    public static <T> StdProvider<T> normalize(Provider<T> provider) {
        if (provider instanceof StdProvider) {
            return (StdProvider<T>) provider;
        } else {
            return new DelegatingStdProvider<>(provider);
        }
    }

    public static <T> Provider<? extends T> instanceProvider(Key<T> key, T instance) {
        Validate.notNull(key, "key");
        Validate.notNull(instance, "instance");
        return new InstanceProvider<>(instance);
    }

    public static <T> Provider<? extends T> providerTypeProvider(TypeReference<? extends Provider<? extends T>> providerClass) {
        Validate.notNull(providerClass);
        return new ProviderTypeProvider<>(providerClass);
    }

    public static <T> Provider<? extends T> link(Key<T> key, Key<? extends T> target) {
        Validate.notNull(key, "key");
        Validate.notNull(target, "target");
        return new LinkedProvider<>(key, target);
    }

}
