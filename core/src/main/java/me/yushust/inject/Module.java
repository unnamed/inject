package me.yushust.inject;

/**
 * This interface represents a part of the {@link Injector}
 * configuration. A module is usually used to bind abstractions
 * (interfaces, abstract classes) to implementations (concrete
 * classes). To just add a scope to a class, you can use scope
 * annotations like {@link javax.inject.Singleton}.
 * You can also use annotations to bind abstractions to implementations
 * like {@link Targetted} and {@link ProvidedBy}.
 *
 * <p>Read about the embedded dsl to configure injector in {@link Binder}.</p>
 *
 * <p>You can also create provider methods (methods used as providers) and
 * annotate it with {@link Provides}. Annotate the method with
 * {@link javax.inject.Inject} if the object creation has dependencies.</p>
 *
 * <p>There's an extension for users to create bindings in a pretty way
 * using {@link AbstractModule}.</p>
 *
 * @see Binder Read the EDSL specification
 */
public interface Module {

    /**
     * Configures all bindings on the specified {@code binder}. This method
     * is called before checking for methods annotated with {@literal @}{@link Provides}
     * to create method-based providers.
     *
     * <p>Don't invoke this method to configure binders. Instead use {@link Binder#install}
     * to check all the methods annotated with {@literal @}{@link Provides}</p>
     *
     * @param binder The configuring binder, attached to an specific injector
     */
    void configure(Binder binder);

}
