package me.yushust.inject.provision.std;

import me.yushust.inject.impl.InjectorImpl;
import me.yushust.inject.impl.ProvisionStack;
import me.yushust.inject.key.Key;
import me.yushust.inject.provision.Providers;
import me.yushust.inject.provision.StdProvider;
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
public class ScopedProvider<T>
    extends StdProvider<T>
    implements InjectionListener, Provider<T> {

  private final Provider<T> unscoped;
  private final Provider<T> scoped;
  private final Scope scope;

  public ScopedProvider(Provider<T> provider, Scope scope) {
    this.unscoped = Validate.notNull(provider, "provider");
    this.scope = Validate.notNull(scope, "scope");
    this.scoped = scope.scope(provider);
  }

  protected ScopedProvider() {
    this.unscoped = null;
    this.scoped = null;
    this.scope = null;
  }

  @Override
  public Provider<T> withScope(Key<?> match, Scope scope) {
    if (this.scope == scope) {
      return this;
    }
    throw new UnsupportedOperationException(
        "Cannot scope the provider again! Scope: " + scope.getClass().getSimpleName()
            + ". Provider: " + unscoped
    );
  }

  @Override
  public void onInject(ProvisionStack stack, InjectorImpl injector) {
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

  public boolean requiresJitScoping() {
    return false;
  }

}
