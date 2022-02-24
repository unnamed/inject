package me.yushust.inject.provision.std.generic;

import me.yushust.inject.key.Key;

import javax.inject.Provider;

/**
 * It's (indirectly) a {@link Provider} for not-bound
 * parameterized types
 *
 * @param <T> The built type
 */
public interface GenericProvider<T> {

    /**
     * Creates an instance of {@link T} using
     * the provided type parameters.
     *
     * @param match The matched type
     */
    T get(Key<?> match);

    /**
     * Converts this {@link GenericProvider} to a
     * constant normal {@link Provider} that always
     * use the given {@code match} key
     */
    default Provider<T> asConstantProvider(Key<?> match) {
        return () -> get(match);
    }

}
