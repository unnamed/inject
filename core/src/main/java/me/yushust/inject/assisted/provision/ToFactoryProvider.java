package me.yushust.inject.assisted.provision;

import me.yushust.inject.assisted.Assisted;
import me.yushust.inject.assisted.FactoryException;
import me.yushust.inject.assisted.ValueFactory;
import me.yushust.inject.error.ErrorAttachable;
import me.yushust.inject.error.ErrorAttachableImpl;
import me.yushust.inject.internal.BinderImpl;
import me.yushust.inject.internal.InternalInjector;
import me.yushust.inject.internal.ProvisionStack;
import me.yushust.inject.key.Key;
import me.yushust.inject.key.TypeReference;
import me.yushust.inject.provision.StdProvider;
import me.yushust.inject.provision.ioc.BindListener;
import me.yushust.inject.provision.ioc.InjectionListener;
import me.yushust.inject.resolve.InjectableConstructor;
import me.yushust.inject.resolve.OptionalDefinedKey;
import me.yushust.inject.util.Validate;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

public class ToFactoryProvider<T>
    extends StdProvider<T>
    implements BindListener, InjectionListener {

  private final Class<? extends ValueFactory> factory;

  public ToFactoryProvider(Class<? extends ValueFactory> factory) {
    this.factory = Validate.notNull(factory, "factory");
  }

  @Override
  public boolean onBind(BinderImpl binder, Key<?> key) {

    ErrorAttachable errors = new ErrorAttachableImpl();
    TypeReference<?> required = key.getType();
    InjectableConstructor constructor = binder.getResolver().getConstructor(errors, required, Assisted.class);

    if (errors.hasErrors() || constructor == null) {
      if (!errors.hasErrors()) {
        errors.attach("Cannot resolve constructor annotated with @Assisted in type " + required);
      }
      throw new FactoryException(errors.formatMessages());
    }

    if (factory.getMethods().length != 1) {
      throw new FactoryException("Factory " + factory.getName() + " has invalid method count");
    }

    Method method = factory.getMethods()[0];

    TypeReference<?> given = TypeReference.of(method.getGenericReturnType());
    if (!given.equals(required)) {
      throw new FactoryException("Method " + method.getName() + " of factory "
          + factory.getName() + " doesn't return " + given);
    }

    List<OptionalDefinedKey<?>> keys = binder.getResolver().keysOf(
        TypeReference.of(factory),
        method.getGenericParameterTypes(),
        method.getParameterAnnotations()
    );

    Set<Key<?>> assists = new HashSet<>();

    for (OptionalDefinedKey<?> parameterKey : keys) {
      if (!assists.add(parameterKey.getKey())) {
        throw new FactoryException("Creator method has two equal assisted values! " +
            "Consider using qualifiers to difference them (key " + parameterKey.getKey() + ")");
      }
    }

    Set<Key<?>> constructorAssists = new HashSet<>();

    for (OptionalDefinedKey<?> parameterKey : constructor.getKeys()) {
      if (parameterKey.isAssisted()) {
        if (!assists.contains(parameterKey.getKey())) {
          throw new FactoryException("Constructor requires assist for "
              + parameterKey.getKey() + " and method doesn't give it!");
        } else if (!constructorAssists.add(parameterKey.getKey())) {
          throw new FactoryException("Constructor has two equal assisted keys! " +
              "Consider using qualifiers to difference them (key " + parameterKey.getKey() + ")");
        }
      }
    }

    if (assists.size() != constructorAssists.size()) {
      throw new FactoryException("Assists mismatch! Constructor has "
          + constructorAssists.size() + " values and method " + assists.size() + " values.");
    }

    binder.$unsafeBind(Key.of(factory), new ProxiedInstanceProvider<T>(
        factory,
        method,
        keys,
        constructor,
        key
    ));
    return false;
  }

  @Override
  public T get() {
    return null;
  }

  @Override
  public void onInject(ProvisionStack stack, InternalInjector injector) {
    // no injects
  }
}
