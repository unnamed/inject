package me.yushust.inject.assisted.provision;

import me.yushust.inject.assisted.Assisted;
import me.yushust.inject.error.FactoryException;
import me.yushust.inject.assisted.ValueFactory;
import me.yushust.inject.impl.BinderImpl;
import me.yushust.inject.key.Key;
import me.yushust.inject.key.TypeReference;
import me.yushust.inject.provision.StdProvider;
import me.yushust.inject.resolve.ComponentResolver;
import me.yushust.inject.resolve.solution.InjectableConstructor;
import me.yushust.inject.key.InjectedKey;
import me.yushust.inject.util.Validate;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Represents a provider of a factory. The keys aren't really
 * bound to this provider. The provider is never put on the
 * bindings map, instead of it, it binds the factory type to
 * a {@link ProxiedFactoryProvider}
 *
 * @param <T> The bound key type
 */
public class ToFactoryProvider<T>
    extends StdProvider<T> {

  private final TypeReference<? extends ValueFactory> factory;

  public ToFactoryProvider(TypeReference<? extends ValueFactory> factory) {
    this.factory = Validate.notNull(factory, "factory");
  }

  @Override
  public boolean onBind(BinderImpl binder, Key<?> key) {

    Class<? extends ValueFactory> factoryRawType = factory.getRawType();
    TypeReference<?> required = key.getType();
    InjectableConstructor constructor = ComponentResolver
        .constructor()
        .resolve(binder, required, Assisted.class);

    // check created object
    if (constructor == null) {
      binder.attach(
          "Bad assisted object",
          new FactoryException("Cannot resolve constructor annotated with @Assisted in type " + required)
      );
      return false;
    }

    // check factory class
    if (!factoryRawType.isInterface()) {
      binder.attach("Factory " + factory
          + " must be an interface with one single method!");
      return false;
    }

    int methodCount = factoryRawType.getMethods().length;
    if (methodCount != 1) {
      binder.attach(
          "Bad factory method",
          new FactoryException("Factory " + factory
              + " has invalid method count (expected: 1, found: " + methodCount + ")")
      );
      return false;
    }

    Method method = factoryRawType.getMethods()[0];
    Type methodReturnType = factory.resolve(method.getGenericReturnType());

    // check return type is equal to the bound key
    if (!required.equals(methodReturnType)) {
      binder.attach(
          "Bad factory method",
          new FactoryException("Method " + method.getName() + " of factory "
              + factory + " must return " + required)
      );
      return false;
    }

    List<InjectedKey<?>> keys = ComponentResolver.keys().keysOf(
        factory,
        method.getParameters()
    );

    Set<Key<?>> assists = new HashSet<>();

    for (InjectedKey<?> parameterKey : keys) {
      if (!assists.add(parameterKey.getKey())) {
        binder.attach(
            "Duplicated factory assisted keys",
            new FactoryException("Creator method has two equal assisted values! " +
                "Consider using qualifiers to difference them (key " + parameterKey.getKey() + ")")
        );
        return false;
      }
    }

    Set<Key<?>> constructorAssists = new HashSet<>();

    for (InjectedKey<?> parameterKey : constructor.getKeys()) {
      if (parameterKey.isAssisted()) {
        if (!assists.contains(parameterKey.getKey())) {
          binder.attach(
              "Unsatisfied Assisted Constructor",
              new FactoryException("Constructor requires assist for "
                  + parameterKey.getKey() + " and method doesn't give it!")
          );
          return false;
        } else if (!constructorAssists.add(parameterKey.getKey())) {
          binder.attach(
              "Duplicated constructor assisted keys",
              new FactoryException("Constructor has two equal assisted keys! " +
                  "Consider using qualifiers to difference them (key " + parameterKey.getKey() + ")")
          );
          return false;
        }
      }
    }

    if (assists.size() != constructorAssists.size()) {
      binder.attach(
          "Assists mismatch, different assisted injections count",
          new FactoryException("Assists mismatch! Constructor has "
              + constructorAssists.size() + " values and method " + assists.size() + " values.")
      );
      return false;
    }

    @SuppressWarnings("unchecked")
    Key<T> castedKey = (Key<T>) key;
    binder.$unsafeBind(Key.of(factory), new ProxiedFactoryProvider<>(
        factoryRawType,
        method,
        keys,
        constructor,
        castedKey
    ));
    return false;
  }

  @Override
  public T get() {
    throw new IllegalStateException("The instance is bound to a Factory, you must get an instance of that factory!");
  }

}
