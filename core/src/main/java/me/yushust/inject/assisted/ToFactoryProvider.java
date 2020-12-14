package me.yushust.inject.assisted;

import me.yushust.inject.error.ErrorAttachable;
import me.yushust.inject.error.ErrorAttachableImpl;
import me.yushust.inject.internal.BinderImpl;
import me.yushust.inject.internal.InjectedProvider;
import me.yushust.inject.internal.InternalInjector;
import me.yushust.inject.internal.ProvisionStack;
import me.yushust.inject.key.Key;
import me.yushust.inject.key.TypeReference;
import me.yushust.inject.provision.BindListener;
import me.yushust.inject.resolve.InjectableConstructor;
import me.yushust.inject.resolve.OptionalDefinedKey;
import me.yushust.inject.util.Validate;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ToFactoryProvider<T>
    extends InjectedProvider<T>
    implements BindListener {

  private final Class<? extends ValueFactory> factory;

  public ToFactoryProvider(Class<? extends ValueFactory> factory) {
    super(() -> null);
    this.factory = Validate.notNull(factory, "factory");
  }

  @Override
  public void onBind(BinderImpl binder, Key<?> key) {

    ErrorAttachable errors = new ErrorAttachableImpl();
    TypeReference<?> required = key.getType();
    InjectableConstructor constructor = binder.getResolver().getConstructor(errors, required, Assisted.class);

    if (errors.hasErrors() || constructor == null) {
      if (!errors.hasErrors()) {
        errors.attach("Cannot resolve constructor annotated with @Assisted in type " + required);
      }
      throw new FactoryException(errors.formatMessages());
    }

    String creatorMethod = null;

    for (Method method : factory.getMethods()) {
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

      creatorMethod = method.getName();
      break;
    }

    if (creatorMethod == null) {
      throw new FactoryException("Factory " + factory.getName() + " has no creator method");
    }

    binder.$unsafeBind(Key.of(factory), new ProxiedInstanceProvider<>(key, creatorMethod));
  }

  private class ProxiedInstanceProvider<O> extends InjectedProvider<O> {

    private final Key<O> key;
    private final String creatorMethodName;
    private Object factoryInstance;

    public ProxiedInstanceProvider(Key<O> key, String creatorMethodName) {
      super(() -> null);
      this.key = key;
      this.creatorMethodName = creatorMethodName;
    }

    @Override
    public void inject(ProvisionStack stack, InternalInjector injector) {
      factoryInstance = Proxy.newProxyInstance(
          getClass().getClassLoader(),
          new Class[] {factory},
          (proxy, method, args) -> {
            if (method.getName().equals(creatorMethodName)) {
              return injector.getInstance(injector.stackForThisThread(), key, false);
            }
            return null;
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

  @Override
  public void inject(ProvisionStack stack, InternalInjector injector) {

  }

}
