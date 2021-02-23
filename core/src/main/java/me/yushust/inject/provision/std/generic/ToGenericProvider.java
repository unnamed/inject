package me.yushust.inject.provision.std.generic;

import me.yushust.inject.provision.std.ScopedProvider;
import me.yushust.inject.impl.BinderImpl;
import me.yushust.inject.impl.InjectorImpl;
import me.yushust.inject.impl.ProvisionStack;
import me.yushust.inject.key.Key;
import me.yushust.inject.provision.StdProvider;
import me.yushust.inject.provision.ioc.BindListener;
import me.yushust.inject.provision.ioc.ScopeListener;
import me.yushust.inject.scope.Scope;
import me.yushust.inject.util.Validate;

import javax.inject.Provider;

public class ToGenericProvider<T>
    extends ScopedProvider<T>
    implements BindListener, ScopeListener<T> {

  private final GenericProvider<T> provider;
  private Scope scope;

  public ToGenericProvider(GenericProvider<T> provider) {
    this.provider = Validate.notNull(provider, "provider");
  }

  @Override
  public void onInject(ProvisionStack stack, InjectorImpl injector) {
    // don't inject null references
  }

  @Override
  public boolean onBind(BinderImpl binder, Key<?> key) {

    boolean isRawType = key.isPureRawType();

    if (!isRawType) {
      binder.attach("You must bound the raw-type to a GenericProvider, " +
          "not a parameterized type! (key: " + key + ", genericProvider: " + provider + ")");
    }

    return isRawType;
  }

  @Override
  public T get() {
    throw new IllegalStateException("Key was bound to a generic provider," +
        " it cannot complete a raw-type!\n\tProvider: " + provider);
  }

  /**
   * Special injector case for keys bound
   * to generic providers
   */
  @Override
  public T get(Key<?> bound) {
    return provider.get(bound);
  }

  @Override
  public Provider<T> withScope(Key<?> match, Scope scope) {
    if (scope != null) {
      this.scope = scope;
    }
    if (match.isPureRawType()) {
      return this;
    } else {
      return new SyntheticGenericProvider(
          match,
          scope == null ? this.scope : scope
      );
    }
  }

  @Override
  public boolean requiresJitScoping() {
    return true;
  }

  public class SyntheticGenericProvider
      extends StdProvider<T>
      implements ScopeListener<T> {

    private final Scope scope;
    private final Provider<T> scoped;

    public SyntheticGenericProvider(Key<?> match, Scope scope) {
      this.scope = scope;
      Provider<T> unscoped = ToGenericProvider.this.provider.asConstantProvider(match);
      this.scoped = scope == null ? unscoped : scope.scope(unscoped);
      setInjected(true);
    }

    @Override
    public T get() {
      return scoped.get();
    }

    @Override
    public Provider<T> withScope(Key<?> match, Scope scope) {
      Validate.argument(this.scope == scope, "Not the same scope on GenericProvider!");
      return new SyntheticGenericProvider(match, scope);
    }
  }

}
