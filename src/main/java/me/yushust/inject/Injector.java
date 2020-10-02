package me.yushust.inject;

import me.yushust.inject.key.TypeReference;

public interface Injector extends MembersInjector<Object> {

  /**
   * @return True if the debug mode
   * is enabled
   */
  boolean isDebugEnabled();

  /**
   * @return Toggles the debug mode
   * for this injector
   */
  Injector toggleDebug();

  /**
   * Injects the static members of the specified class,
   * equivalent to execute the overloaded method
   * {@link Injector#injectMembers(TypeReference, Object)}
   * passing a null instance and the type reference
   * created with the provided class
   *
   * @param clazz The class
   */
  void injectStaticMembers(Class<?> clazz);

  /**
   * Lowest Level function of {@link Injector}, resolves the specified type
   * and calls to a {@link MembersInjector} to inject fields and methods in
   * the specified instance, if the instance is null, the injector handles
   * it like a static injection
   * @param type     The injected type
   * @param instance The object that will be injected,
   *                 if it's null, the injector handles
   *                 it like a static injection
   * @param <T>      The type of the injected object
   */
  <T> void injectMembers(TypeReference<T> type, T instance);

  /**
   * Converts the specified class to a {@link TypeReference}
   * and calls the overloaded method {@link Injector#getInstance(TypeReference)}
   *
   * @param type The instance class
   * @param <T>  The class parameter
   * @return The instance, or null if the specified class
   * isn't injectable
   */
  <T> T getInstance(Class<T> type);

  /**
   * Lowest-level method of {@link Injector} for instantiating
   * and injecting a class.
   *
   * @param type The instance generic type
   * @param <T>  The type parameter
   * @return The instance, or null if the class isn't injectable
   */
  <T> T getInstance(TypeReference<T> type);

}
