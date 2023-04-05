package team.unnamed.inject.scope;

import team.unnamed.inject.Provider;

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
     * @see LazySingletonScope
     * @see Scopes
     */
    <T> Provider<T> scope(Provider<T> unscoped);

}
