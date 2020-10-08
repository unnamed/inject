package me.yushust.inject.internal;

import me.yushust.inject.Injector;
import me.yushust.inject.key.Key;
import me.yushust.inject.key.TypeReference;
import me.yushust.inject.resolve.*;
import me.yushust.inject.util.Validate;

import javax.inject.Inject;
import javax.inject.Provider;
import java.lang.reflect.Modifier;
import java.util.List;

public class InjectorImpl extends InternalInjector implements Injector {

  private final MembersResolver membersResolver;
  private final BinderImpl binder;

  public InjectorImpl(MembersResolver membersResolver, BinderImpl binder) {
    this.membersResolver = Validate.notNull(membersResolver);
    this.binder = Validate.notNull(binder);
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
  protected <T> void injectMembers(ProvisionStack stack, Key<T> type, T instance) {
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
  protected <T> T getInstance(ProvisionStack stack, Key<T> type, boolean useExplicitBindings) {
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
    // If the stack isn't empty, it's a recursive call.
    // If the key is present in the provision stack,
    // we can return the stored instance instead of creating
    // another instance
    if (stack.has(type)) {
      // It's a cyclic dependency and it's now fixed
      return stack.get(type);
    }

    if (useExplicitBindings) {
      Provider<T> provider = getProviderAndInject(stack, type);
      if (provider != null) {
        return provider.get();
      }
    }

    InjectableConstructor constructor = membersResolver.getConstructor(stack, type.getType());
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

  private <T> Provider<T> getProviderAndInject(ProvisionStack stack, Key<T> key) {
    @SuppressWarnings("unchecked")
    InjectedProvider<T> provider = (InjectedProvider<T>) binder.getProvider(key);
    if (provider == null) {
      return null;
    }
    if (!provider.isInjected()) {
      Provider<? extends T> delegated = provider.getDelegate();
      injectMembers(stack, Key.of(TypeReference.of(delegated.getClass())), delegated);
      provider.setInjected(true);
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
      Object value = getInstance(stack, key.getKey(), true);
      List<String> snapshot = stack.getErrorMessages();
      if (value == null && !key.isOptional()) {
        stack.attach(
            "Cannot inject " + member + ":\n"
                + "    Reason: Cannot get an instance for key, and injection isn't optional\n"
                + "    Key: " + key.getKey()
        );
      } else {
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
