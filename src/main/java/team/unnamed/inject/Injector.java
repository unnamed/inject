package team.unnamed.inject;

import team.unnamed.inject.impl.BinderImpl;
import team.unnamed.inject.impl.InjectorImpl;
import team.unnamed.inject.key.TypeReference;

import java.util.Arrays;

public interface Injector {

    static Injector create(Module... modules) {
        return create(Arrays.asList(modules));
    }

    static Injector create(Iterable<? extends Module> modules) {
        BinderImpl binder = new BinderImpl();
        binder.install(modules);
        if (binder.hasErrors()) {
            binder.reportAttachedErrors();
        }
        return new InjectorImpl(binder);
    }

    /**
     * Returns the explicit bound provider for the specified key
     */
    default <T> Provider<? extends T> getProvider(Class<T> key) {
        return getProvider(TypeReference.of(key));
    }

    /**
     * Returns the explicit bound provider for the specified key
     */
    <T> Provider<? extends T> getProvider(TypeReference<T> key);

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

    default void injectMembers(Object object) {
        injectMembers(TypeReference.of(object.getClass()), object);
    }

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
    default <T> T getInstance(Class<T> type) {
        return getInstance(TypeReference.of(type));
    }

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
