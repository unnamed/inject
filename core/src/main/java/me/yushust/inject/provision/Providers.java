package me.yushust.inject.provision;

import me.yushust.inject.impl.BinderImpl;
import me.yushust.inject.impl.InjectorImpl;
import me.yushust.inject.impl.ProvisionStack;
import me.yushust.inject.key.Key;
import me.yushust.inject.key.TypeReference;
import me.yushust.inject.provision.ioc.BindListener;
import me.yushust.inject.provision.ioc.InjectionListener;
import me.yushust.inject.provision.ioc.ScopeListener;
import me.yushust.inject.provision.std.InstanceProvider;
import me.yushust.inject.provision.std.LinkedProvider;
import me.yushust.inject.provision.std.ProviderTypeProvider;
import me.yushust.inject.provision.std.ScopedProvider;
import me.yushust.inject.scope.Scope;
import me.yushust.inject.util.Validate;

import javax.inject.Provider;

/**
 * Collection of static factory methods to create providers
 */
public final class Providers {

  private Providers() {
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

  public static <T> Provider<T> scope(Key<?> match, Provider<T> provider, Scope scope) {
    if (provider instanceof ScopeListener) {
      return ((ScopeListener<T>) provider).withScope(match, scope);
    } else {
      StdProvider<T> scopedProvider = new ScopedProvider<>(provider, scope);
      scopedProvider.setInjected(provider instanceof StdProvider && ((StdProvider<T>) provider).isInjected());
      return scopedProvider;
    }
  }

  public static boolean onBind(BinderImpl binder, Key<?> key, Provider<?> provider) {
    if (provider instanceof BindListener) {
      return ((BindListener) provider).onBind(binder, key);
    } else {
      return true;
    }
  }

  public static void inject(InjectorImpl injector, Provider<?> provider) {
    inject(injector, injector.stackForThisThread(), provider);
  }

  public static void inject(InjectorImpl injector, ProvisionStack stack, Provider<?> provider) {
    if (provider instanceof StdProvider) {
      ((StdProvider<?>) provider).setInjected(true);
    }
    if (provider instanceof InjectionListener) {
      ((InjectionListener) provider).onInject(stack, injector);
    } else {
      // TODO: inject(...) method should receive the provider type (for type specificity)
      injector.injectMembers(provider);
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
