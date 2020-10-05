package me.yushust.inject;

import me.yushust.inject.internal.BinderImpl;
import me.yushust.inject.internal.DefaultQualifierFactory;
import me.yushust.inject.internal.InjectorImpl;
import me.yushust.inject.key.TypeReference;
import me.yushust.inject.resolve.CachedMembersResolver;
import me.yushust.inject.resolve.MembersResolver;
import me.yushust.inject.resolve.MembersResolverImpl;
import me.yushust.inject.resolve.QualifierFactory;

import java.util.Arrays;

public interface Injector {

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

  void injectMembers(Object object);

  /**
   * Lowest Level function of {@link Injector}, resolves the specified type
   * and to injects fields and methods in
   * the specified instance, if the instance is null, the injector handles
   * it like a static injection
   *
   * @param type     The injected types
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

  static Injector create(Module... modules) {
    return create(Arrays.asList(modules));
  }

  static Injector create(Iterable<? extends Module> modules) {
    QualifierFactory qualifierFactory = DefaultQualifierFactory.INSTANCE;
    MembersResolver membersResolver = CachedMembersResolver.wrap(
        new MembersResolverImpl(qualifierFactory)
    );
    BinderImpl binder = new BinderImpl(qualifierFactory, membersResolver);
    binder.install(modules);
    if (binder.hasErrors()) {
      binder.reportAttachedErrors();
    }
    return new InjectorImpl(membersResolver, binder);
  }

}
