package me.yushust.inject.assisted.provision;

import me.yushust.inject.assisted.ValueFactory;
import me.yushust.inject.internal.InternalInjector;
import me.yushust.inject.internal.ProvisionStack;
import me.yushust.inject.key.Key;
import me.yushust.inject.key.TypeReference;
import me.yushust.inject.provision.StdProvider;
import me.yushust.inject.provision.ioc.InjectionListener;
import me.yushust.inject.resolve.InjectableConstructor;
import me.yushust.inject.resolve.InjectableMethod;
import me.yushust.inject.resolve.OptionalDefinedKey;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ProxiedInstanceProvider<O>
    extends StdProvider<O>
    implements InjectionListener {

  private final Class<? extends ValueFactory> factory;
  private final Method method;
  private final List<OptionalDefinedKey<?>> keys;
  private final InjectableConstructor constructor;
  private final Key<?> key;
  private Object factoryInstance;

  ProxiedInstanceProvider(
      Class<? extends ValueFactory> factory,
      Method method,
      List<OptionalDefinedKey<?>> keys,
      InjectableConstructor constructor,
      Key<?> key
  ) {
    this.factory = factory;
    this.method = method;
    this.keys = keys;
    this.constructor = constructor;
    this.key = key;
  }

  private Object createInstance(InternalInjector injector, Object[] extras) {

    Map<Key<?>, Object> values = new HashMap<>();
    for (int i = 0; i < extras.length; i++) {
      Key<?> valueKey = keys.get(i).getKey();
      Object value = extras[i];
      values.put(valueKey, value);
    }

    Object[] givenArgs = new Object[constructor.getKeys().size()];

    int i = 0;
    for (OptionalDefinedKey<?> injection : constructor.getKeys()) {
      if (injection.isAssisted()) {
        givenArgs[i] = values.get(injection.getKey());
      } else {
        Object val = injector.getInstance(injector.stackForThisThread(), injection.getKey(), true);
        givenArgs[i] = val;
      }
      i++;
    }

    Object instance = constructor.createInstance(injector.stackForThisThread(), givenArgs);
    injector.injectMembers((TypeReference) key.getType(), instance);
    return instance;
  }

  @Override
  public void onInject(ProvisionStack stack, InternalInjector injector) {
    factoryInstance = Proxy.newProxyInstance(
        getClass().getClassLoader(),
        new Class[]{factory},
        (proxy, method, args) -> {
          if (method.equals(this.method)) {
            return createInstance(injector, args);
          } else {
            return null;
          }
        }
    );
  }

  @Override
  public O get() {
    @SuppressWarnings("unchecked")
    O value = (O) factoryInstance;
    return value;
  }
}
