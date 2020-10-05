package me.yushust.inject.internal;

import me.yushust.inject.Injector;
import me.yushust.inject.error.ErrorAttachable;
import me.yushust.inject.error.ErrorAttachableImpl;
import me.yushust.inject.error.ErrorProne;
import me.yushust.inject.key.Key;
import me.yushust.inject.key.TypeReference;
import me.yushust.inject.resolve.InjectableConstructor;
import me.yushust.inject.resolve.InjectableMember;
import me.yushust.inject.resolve.MembersResolver;
import me.yushust.inject.resolve.OptionalDefinedKey;
import me.yushust.inject.util.Validate;

import javax.inject.Provider;
import java.util.List;

public class InjectorImpl extends InternalInjector implements Injector {

  private final MembersResolver membersResolver;
  private final BinderImpl binder;

  public InjectorImpl(MembersResolver membersResolver, BinderImpl binder) {
    this.membersResolver = Validate.notNull(membersResolver);
    this.binder = Validate.notNull(binder);
  }

  public void injectStaticMembers(Class<?> clazz) {
    throw new UnsupportedOperationException("This method isn't supported yet");
  }

  @ThreadSensitive
  protected <T> ErrorAttachable injectMembers(Key<T> type, T instance) {
    ErrorAttachable errors = new ErrorAttachableImpl();
    ProvisionStack stack = stackForThisThread();
    stack.add(type, instance);
    for (InjectableMember member : membersResolver.getFields(type.getType())) {
      injectToMember(errors, instance, member);
    }
    for (InjectableMember member : membersResolver.getMethods(type.getType())) {
      injectToMember(errors, instance, member);
    }
    stack.removeFirst();
    return errors;
  }

  @ThreadSensitive
  protected <T> ErrorProne<T> getInstance(Key<T> type, boolean useExplicitBindings) {
    Class<? super T> rawType = type.getType().getRawType();
    // Default injections
    if (
        rawType == Injector.class
        || rawType == InternalInjector.class
    ) {
      @SuppressWarnings("unchecked")
      T value = (T) this;
      return new ErrorProne<>(value);
    }
    ProvisionStack stack = stackForThisThread();
    // If the stack isn't empty, it's a recursive call.
    // If the key is present in the provision stack,
    // we can return the stored instance instead of creating
    // another instance
    if (stack.has(type)) {
      // It's a cyclic dependency and it's now fixed
      return new ErrorProne<>(stack.get(type));
    }

    if (useExplicitBindings) {
      Provider<T> provider = getProviderAndInject(type);
      if (provider != null) {
        return new ErrorProne<>(provider.get());
      }
    }

    ErrorAttachable errors = new ErrorAttachableImpl();
    InjectableConstructor constructor = membersResolver.getConstructor(type.getType());
    Object instance = constructor.createInstance(
        errors,
        getValuesForKeys(
            constructor.getKeys(),
            constructor,
            errors
        )
    );
    @SuppressWarnings("unchecked")
    T value = (T) instance;

    if (value != null) {
      injectMembers(type, value);
    }

    ErrorProne<T> errorProneValue = new ErrorProne<>(value);
    errorProneValue.attachAll(errors);
    return errorProneValue;
  }

  private void injectToMember(ErrorAttachable errors,
                              Object instance,
                              InjectableMember member) {
    List<OptionalDefinedKey<?>> keys = member.getKeys();
    Object[] values = getValuesForKeys(keys, member, errors);
    member.inject(errors, instance, values);
  }

  private <T> Provider<T> getProviderAndInject(Key<T> key) {
    @SuppressWarnings("unchecked")
    InjectedProvider<T> provider = (InjectedProvider<T>) binder.getProvider(key);
    if (provider == null) {
      return null;
    }
    if (!provider.isInjected()) {
      Provider<? extends T> delegated = provider.getDelegate();
      injectMembers(Key.of(TypeReference.of(delegated.getClass())), delegated);
      provider.setInjected(true);
    }
    return provider;
  }

  private Object[] getValuesForKeys(List<OptionalDefinedKey<?>> keys, Object member, ErrorAttachable errors) {
    Object[] values = new Object[keys.size()];
    for (int i = 0; i < keys.size(); i++) {
      OptionalDefinedKey<?> key = keys.get(i);
      // We don't need to clone the stack,
      // the type-instance relations are
      // removes automatically when ended
      // with the injection
      ErrorProne<?> errorProne = getInstance(key.getKey(), true);
      Object value = errorProne.getValue();
      if (errorProne.hasErrors()) {
        errors.attachAll(errorProne);
      }
      if (value == null && !key.isOptional()) {
        errors.attach(
            "Cannot inject " + member + ":\n"
                + "    Reason: Cannot get an instance for key, and injection isn't optional"
                + "    Key: " + key.getKey()
        );
      }
      values[i] = value;
    }
    return values;
  }

}
