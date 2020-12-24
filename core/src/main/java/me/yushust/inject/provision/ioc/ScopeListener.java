package me.yushust.inject.provision.ioc;

import me.yushust.inject.key.Key;
import me.yushust.inject.provision.std.InstanceProvider;
import me.yushust.inject.scope.Scope;

import javax.inject.Provider;

/**
 * Adds the control of scoping the provider. Used by scoped
 * providers (that check if the scope is the same, and return
 * the same provider) and some other providers like {@link InstanceProvider}
 * that doesn't support scopes and throws an exception.
 *
 * <p>This listener also extends {@link Provider} for type
 * safety, guaranteeing that the class that implements this
 * listener is a provider of the expected type {@code T}</p>
 *
 * @param <T> The provider return type
 */
public interface ScopeListener<T> extends Provider<T> {

  /**
   * Scopes the provider with the given {@code scope}
   * @return The scope applied to the provider, the providers
   * are externally immutable, so this shouldn't modify the
   * real provider and returns another provider or the same provider
   */
  Provider<T> withScope(Key<?> match, Scope scope);

}
