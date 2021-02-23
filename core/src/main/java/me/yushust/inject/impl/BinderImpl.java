package me.yushust.inject.impl;

import me.yushust.inject.Binder;
import me.yushust.inject.Module;
import me.yushust.inject.error.BindingException;
import me.yushust.inject.error.ErrorAttachableImpl;
import me.yushust.inject.key.Key;
import me.yushust.inject.key.TypeReference;
import me.yushust.inject.multibinding.MultiBindingBuilderImpl;
import me.yushust.inject.provision.Providers;
import me.yushust.inject.provision.StdProvider;
import me.yushust.inject.provision.std.generic.impl.TypeReferenceGenericProvider;
import me.yushust.inject.provision.std.MethodAsProvider;
import me.yushust.inject.util.Validate;

import javax.inject.Provider;
import java.util.HashMap;
import java.util.Map;

public class BinderImpl extends ErrorAttachableImpl implements Binder {

  private final Map<Key<?>, Provider<?>> bindings =
      new HashMap<>();

  public BinderImpl() {
    // soft
    bind(TypeReference.class).toGenericProvider(new TypeReferenceGenericProvider()).singleton();
  }

  public <T> StdProvider<T> getProvider(Key<T> key) {
    // it's safe, the providers are setted
    // after (provider -> injected provider) conversion
    @SuppressWarnings("unchecked")
    StdProvider<T> provider =
        (StdProvider<T>) this.bindings.get(key);
    return provider;
  }

  @Override
  public void $unsafeBind(Key<?> key, Provider<?> provider) {
    Validate.notNull(key, "key");
    Validate.notNull(provider, "provider");
    if (Providers.onBind(this, key, provider)) {
      this.bindings.put(key, Providers.normalize(provider));
    }
  }

  @Override
  public <T> QualifiedBindingBuilder<T> bind(TypeReference<T> keyType) {
    return new BindingBuilderImpl<>(this, keyType);
  }

  @Override
  public <T> Binder.MultiBindingBuilder<T> multibind(TypeReference<T> keyType) {
    return new MultiBindingBuilderImpl<>(this, keyType);
  }

  /** Throws the errors attached to this attachable */
  @Override
  public void reportAttachedErrors() {
    if (hasErrors()) {
      throw new BindingException(formatMessages());
    }
  }

  @Override
  public void install(Iterable<? extends Module> modules) {
    for (Module module : modules) {
      // configure the manual bindings
      module.configure(this);

      // resolve the provider methods
      MethodAsProvider.resolveMethodProviders(
          this,
          TypeReference.of(module.getClass()),
          module
      ).forEach(this::$unsafeBind);
    }
  }

}
