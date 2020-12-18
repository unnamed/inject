package me.yushust.inject;

import me.yushust.inject.key.TypeReference;

import javax.inject.Provider;

/**
 * It's (indirectly) a {@link Provider} for not-bound
 * parameterized types
 * @param <T> The built type
 */
public interface GenericProvider<T> {

  /**
   * Creates an instance of {@link T} using
   * the provided type parameters.
   * @param rawType The parameterized-type raw type
   * @param parameters The parameterized-type parameters
   */
  T get(Class<?> rawType, TypeReference<?>[] parameters);

}
