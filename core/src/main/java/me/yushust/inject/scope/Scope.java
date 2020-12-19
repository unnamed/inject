package me.yushust.inject.scope;

import javax.inject.Provider;

/**
 * Wraps providers
 */
public interface Scope {

  /**
   * Wraps the provider
   *
   * @param unscoped The unscoped provider
   * @param <T>      The key type
   * @return The wrapped provider
   * @see SingletonScope
   * @see Scopes
   */
  <T> Provider<T> scope(Provider<T> unscoped);

}
