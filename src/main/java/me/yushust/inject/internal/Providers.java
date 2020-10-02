package me.yushust.inject.internal;

import me.yushust.inject.key.Key;
import me.yushust.inject.key.TypeReference;
import me.yushust.inject.util.Validate;

import javax.inject.Inject;
import javax.inject.Provider;

public final class Providers {

  private Providers() {
    throw new UnsupportedOperationException("This class couldn't be instantiated!");
  }

  private static class InstanceProvider<T> implements Provider<T> {

    private final Key<T> key;
    private final T instance;

    private InstanceProvider(Key<T> key, T instance) {
      this.key = Validate.notNull(key, "key");
      this.instance = Validate.notNull(instance, "instance");
    }

    @Inject
    public void inject(InternalInjector injector) {
      injector.injectMembers(key.withNoQualifiers(), instance);
    }

    public T get() {
      return instance;
    }
  }

  public static <T> Provider<? extends T> instanceProvider(Key<T> key, T instance) {
    return new InstanceProvider<T>(key, instance);
  }

  private static class ProviderTypeProvider<T> implements Provider<T> {

    private final TypeReference<? extends Provider<? extends T>> providerClass;
    private volatile Provider<? extends T> provider;

    public ProviderTypeProvider(TypeReference<? extends Provider<? extends T>> providerClass) {
      this.providerClass = Validate.notNull(providerClass, "providerClass");
    }

    @Inject
    public void inject(InternalInjector injector) {
      Key<? extends Provider<? extends T>> key = Key.of(providerClass);
      provider = injector.getInstance(key, false).getValue();
    }

    public T get() {
      if (provider == null) {
        return null;
      }
      return provider.get();
    }
  }

  public static <T> Provider<? extends T> providerTypeProvider(TypeReference<? extends Provider<? extends T>> providerClass) {
    return new ProviderTypeProvider<T>(providerClass);
  }

  private static class SelfReferredProvider<T> implements Provider<T> {

    private final Key<T> key;
    @Inject private InternalInjector injector;

    private SelfReferredProvider(Key<T> key) {
      this.key = Validate.notNull(key, "key");
    }

    public T get() {
      return injector.getInstance(key, true)
          .getValue();
    }
  }

  public static <T> Provider<? extends T> selfReferredProvider(Key<T> key) {
    return new SelfReferredProvider<T>(key);
  }

}
