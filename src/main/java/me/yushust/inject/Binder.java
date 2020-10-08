package me.yushust.inject;

import me.yushust.inject.error.ErrorAttachable;
import me.yushust.inject.key.TypeReference;
import me.yushust.inject.scope.Scope;

import javax.inject.Provider;
import java.lang.annotation.Annotation;

// TODO: Add the EDSL specification
public interface Binder extends ErrorAttachable {

  <T> Qualified<T> bind(Class<T> keyType);

  <T> Qualified<T> bind(TypeReference<T> keyType);

  void install(Module... modules);

  void install(Iterable<? extends Module> modules);

  interface Scoped {

    void in(Scope scope);

    /**
     * Scopes the binding to the
     * singleton scope, a method alias for
     * {@code in(Scopes.SINGLETON)}
     */
    void singleton();

  }

  interface Qualified<T> extends Scoped {

    // qualifying key start
    /**
     * Adds a type-qualifier strategy to the
     * binding key.
     *
     * <p>The qualifier type must be
     * annotated with {@link javax.inject.Qualifier},
     * else, an error is added to the binder and
     * thrown when the configuration stage ends.</p>
     *
     * @param qualifierType The qualifier type
     */
    Qualified<T> markedWith(Class<? extends Annotation> qualifierType);

    /**
     * Adds an instance-qualifier strategy to the
     * binding key.
     *
     * <p>The qualifier type must be
     * annotated with {@link javax.inject.Qualifier},
     * else, an error is added to the binder and
     * thrown when the configuration stage ends.</p>
     *
     * @param annotation The qualifier instance
     */
    Qualified<T> qualified(Annotation annotation);

    /**
     * Adds an instance-qualifier strategy to the
     * binding key, using as annotation {@link javax.inject.Named}
     * and as annotation value the specified {@code name}.
     *
     * It's a method alias for {@code qualified(Qualifiers.createNamed(name))}
     * @param name The {@link javax.inject.Named} annotation value
     */
    Qualified<T> named(String name);
    // qualifying key end

    // linking key start
    Scoped to(Class<? extends T> targetType);

    Scoped to(TypeReference<? extends T> targetType);

    Scoped toProvider(Provider<? extends T> provider);

    <P extends Provider<? extends T>> Scoped toProvider(Class<P> providerClass);

    <P extends Provider<? extends T>> Scoped toProvider(TypeReference<P> providerClass);

    void toInstance(T instance);
    // linking key end

  }

}
