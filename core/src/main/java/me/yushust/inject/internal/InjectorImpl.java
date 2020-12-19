package me.yushust.inject.internal;

import me.yushust.inject.Injector;
import me.yushust.inject.Property;
import me.yushust.inject.PropertyHolder;
import me.yushust.inject.key.Key;
import me.yushust.inject.key.Qualifier;
import me.yushust.inject.key.TypeReference;
import me.yushust.inject.provision.Providers;
import me.yushust.inject.provision.StdProvider;
import me.yushust.inject.provision.ioc.MatchListener;
import me.yushust.inject.provision.std.ToGenericProvider;
import me.yushust.inject.resolve.*;
import me.yushust.inject.util.Validate;

import javax.inject.Inject;
import javax.inject.Provider;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class InjectorImpl extends InternalInjector implements Injector {

  private final MembersResolver membersResolver;
  private final BinderImpl binder;

  public InjectorImpl(MembersResolver membersResolver, BinderImpl binder) {
    this.membersResolver = Validate.notNull(membersResolver);
    this.binder = Validate.notNull(binder);
  }

  @Override
  public <T> Provider<? extends T> getProvider(TypeReference<T> key) {
    return binder.getProvider(Key.of(key));
  }

  @Override
  public void injectStaticMembers(Class<?> clazz) {
    boolean stackWasNotPresent = provisionStackThreadLocal.get() == null;
    injectMembers(stackForThisThread(), Key.of(TypeReference.of(clazz)), null);
    if (stackWasNotPresent) {
      removeStackFromThisThread();
    }
  }

  @ThreadSensitive
  @Override
  public <T> void injectMembers(ProvisionStack stack, Key<T> type, T instance) {
    if (instance != null) {
      stack.push(type, instance);
    }
    for (InjectableMember member : membersResolver.getFields(type.getType())) {
      injectToMember(stack, instance, member);
    }
    for (InjectableMember member : membersResolver.getMethods(type.getType(), Inject.class)) {
      injectToMember(stack, instance, member);
    }
    if (instance != null) {
      stack.pop();
    }
  }

  @ThreadSensitive
  @Override
  public <T> T getInstance(ProvisionStack stack, Key<T> type, boolean useExplicitBindings) {
    Class<? super T> rawType = type.getType().getRawType();
    // Default injections
    if (
        rawType == Injector.class
        || rawType == InternalInjector.class
        || rawType == InjectorImpl.class
    ) {
      @SuppressWarnings("unchecked")
      T value = (T) this;
      return value;
    }

    if (rawType == TypeReference.class) {
      Type ref = type.getType().getType();
      if (!(ref instanceof ParameterizedType)) {
        stack.attach("Cannot inject a non-specific TypeReference " + ref);
        return null;
      }
      ParameterizedType parameterizedType = (ParameterizedType) ref;
      @SuppressWarnings("unchecked")
      T value = (T) TypeReference.of(parameterizedType.getActualTypeArguments()[0]);
      return value;
    }

    String path = getPropertyPath(type);
    if (path != null) {
      // We know the type and there's no necessity to invoke
      // getInstance(...) again, because it's an interface
      // (it cannot be instantiated), the unique way to get
      // an instance of PropertyHolder is with an explicit binding
      Provider<PropertyHolder> provider =
          getProviderAndInject(stack, Key.of(PropertyHolder.class));

      PropertyHolder propertyHolder;

      // An injection request for properties has been made and there's
      // no a properties source!
      if (provider == null ||
          (propertyHolder = provider.get()) == null) {
        stack.attach("There's no a PropertyHolder bound and a" +
            " member annotated with @Property exists! " + type);
        return null;
      }

      Object propertyValue = tryConvert(rawType, propertyHolder.get(path));
      // Incompatible types!
      if (!rawType.isInstance(propertyValue)) {
        stack.attach("The property value in '" + path
            + "' obtained isn't an instance of " + type.getType() + ".");
        return null;
      }

      @SuppressWarnings("unchecked")
      T value = (T) propertyValue;
      return value;
    }

    // If the stack isn't empty, it's a recursive call.
    // If the key is present in the provision stack,
    // we can return the stored instance instead of creating
    // another instance
    if (stack.has(type)) {
      // It's a cyclic dependency and it's now fixed
      return stack.get(type);
    }

    AnnotationScanner.bind(type.getType(), binder);
    AnnotationScanner.scope(type.getType(), binder);
    if (useExplicitBindings) {
      Provider<T> provider = getProviderAndInject(stack, type);
      if (provider != null) {
        if (provider instanceof MatchListener) {
          return ((MatchListener<T>) provider).get(type);
        } else {
          return provider.get();
        }
      }
    }

    InjectableConstructor constructor = membersResolver.getConstructor(stack, type.getType(), Inject.class);
    if (constructor == null) { // the errors are thrown by the caller
      return null;
    }

    Object instance = constructor.createInstance(
        stack,
        getValuesForKeys(
            constructor.getKeys(),
            constructor,
            stack
        )
    );

    @SuppressWarnings("unchecked")
    T value = (T) instance;

    if (value != null) {
      injectMembers(stack, type, value);
    }

    return value;
  }

  Object injectToMember(ProvisionStack stack,
                              Object instance,
                              InjectableMember member) {
    boolean isStatic = Modifier.isStatic(member.getMember().getModifiers());
    if (
        (instance == null && isStatic)
        || (instance != null && !isStatic)
    ) {
      List<OptionalDefinedKey<?>> keys = member.getKeys();
      Object[] values = getValuesForKeys(keys, member, stack);
      return member.inject(stack, instance, values);
    } else {
      return null;
    }
  }

  /**
   * Gets the property path for the specified key,
   * if the key isn't qualified with &#64;Property,
   * returns null.
   */
  private String getPropertyPath(Key<?> key) {
    for (Qualifier qualifier : key.getQualifiers()) {
      if (qualifier.raw() instanceof Property) {
        return ((Property) qualifier.raw()).value();
      }
    }
    return null;
  }

  private Object tryConvert(Class<?> requiredType, Object object) {
    if (object == null) {
      return null;
    } else if (requiredType == String.class) {
      return String.valueOf(object);
    } else if (requiredType == Boolean.class) {
      String value = String.valueOf(object);
      if (value.equalsIgnoreCase("true")) {
        return true;
      } else if (value.equalsIgnoreCase("false")) {
        return false;
      }
    }
    return object;
  }

  private <T> Provider<T> getProviderAndInject(ProvisionStack stack, Key<T> key) {
    @SuppressWarnings("unchecked")
    StdProvider<T> provider = (StdProvider<T>) binder.getProvider(key);
    if (provider == null) {
      Class<?> rawType = key.getType().getRawType();
      if (key.getType().getType() != rawType) {
        @SuppressWarnings("unchecked")
        StdProvider<T> rawTypeProvider = (StdProvider<T>) binder.getProvider(Key.of(rawType));
        if (rawTypeProvider instanceof ToGenericProvider) {
          provider = rawTypeProvider;
        } else {
          return null;
        }
      } else {
        return null;
      }
    }
    if (!provider.isInjected()) {
      Providers.inject(this, stack, provider);
    }
    return provider;
  }

  private Object[] getValuesForKeys(List<OptionalDefinedKey<?>> keys, Object member, ProvisionStack stack) {
    Object[] values = new Object[keys.size()];
    for (int i = 0; i < keys.size(); i++) {
      OptionalDefinedKey<?> key = keys.get(i);
      // We don't need to clone the stack,
      // the type-instance relations are
      // removes automatically when ended
      // with the injection
      List<String> snapshot = stack.getErrorMessages();
      Object value = getInstance(stack, key.getKey(), true);
      if (value == null && !key.isOptional()) {
        stack.attach("Cannot inject " + member + ":\n"
            + "    Reason: Cannot get an instance for key, and injection isn't optional\n"
            + "    Key: " + key.getKey());
      } else if (key.isOptional()) {
        // remove errors because the injection
        // is optional and we don't need a report
        // of fails that can be valid
        stack.applySnapshot(snapshot);
      }
      values[i] = value;
    }
    return values;
  }

}
