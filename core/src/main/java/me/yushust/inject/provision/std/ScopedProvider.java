package me.yushust.inject.provision.std;

import me.yushust.inject.internal.InternalInjector;
import me.yushust.inject.internal.ProvisionStack;
import me.yushust.inject.key.Key;
import me.yushust.inject.provision.Providers;
import me.yushust.inject.provision.StdProvider;
import me.yushust.inject.provision.ioc.InjectionListener;
import me.yushust.inject.provision.ioc.MatchListener;
import me.yushust.inject.provision.ioc.ScopeListener;
import me.yushust.inject.scope.Scope;
import me.yushust.inject.util.Validate;

import javax.inject.Provider;

/**
 * It's a provider wrapped. Maintains the
 * unscoped provider, the scoped provider
 * and the scope.
 *
 * <p>The providers cannot be re-scoped</p>
 * @param <T> The provider return type
 */
public final class ScopedProvider<T>
    extends StdProvider<T>
    implements ScopeListener<T>, InjectionListener, MatchListener<T> {

  private final Provider<T> unscoped;
  private final Provider<T> scoped;
  private final Scope scope;

  public ScopedProvider(Provider<T> provider, Scope scope) {
    this.unscoped = Validate.notNull(provider, "provider");
    this.scope = Validate.notNull(scope, "scope");
    this.scoped = scope.scope(provider);
  }

  @Override
  public Provider<T> withScope(Scope scope) {
    throw new UnsupportedOperationException("Cannot scope the provider again!");
  }

  @Override
  public void onInject(ProvisionStack stack, InternalInjector injector) {
    Providers.inject(injector, stack, unscoped);
    Providers.inject(injector, stack, scoped); // some scopes requires injections
  }

  @Override
  public T get() {
    return scoped.get();
  }

  public Provider<T> getUnscoped() {
    return unscoped;
  }

  public Provider<T> getScoped() {
    return scoped;
  }

  public Scope getScope() {
    return scope;
  }

  @Override
  public T get(Key<?> match) {
    if (scoped instanceof MatchListener) {
      return ((MatchListener<T>) scoped).get(match);
    } else {
      return scoped.get();
    }
  }
}
