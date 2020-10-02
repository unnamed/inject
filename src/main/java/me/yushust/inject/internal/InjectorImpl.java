package me.yushust.inject.internal;

import me.yushust.inject.Injector;
import me.yushust.inject.error.ErrorAttachable;
import me.yushust.inject.error.ErrorAttachableImpl;
import me.yushust.inject.error.ErrorProne;
import me.yushust.inject.key.Key;
import me.yushust.inject.key.Qualifier;
import me.yushust.inject.key.TypeReference;
import me.yushust.inject.resolve.*;
import me.yushust.inject.util.Validate;

import javax.inject.Provider;
import java.util.List;

public class InjectorImpl extends InternalInjector implements Injector {

  private final MembersBox membersBox;
  private final BinderImpl binder;

  public InjectorImpl(MembersBox membersBox, BinderImpl binder) {
    this.membersBox = Validate.notNull(membersBox);
    this.binder = Validate.notNull(binder);
  }

  public void injectStaticMembers(Class<?> clazz) {
    throw new UnsupportedOperationException("This method isn't supported yet");
  }

  protected <T> ErrorAttachable injectMembers(Key<T> type, T instance) {
    ErrorAttachable errors = new ErrorAttachableImpl();
    ProvisionStack stack = stackForThisThread();
    stack.add(type, instance);
    // Injector doesn't know if the member is a field
    // or method, it doesn't matter
    for (InjectableMember member : membersBox.getMembers(type.getType())) {
      List<OptionalDefinedKey<?>> keys = member.getKeys();
      Object[] values = getValuesForKeys(keys, member, errors);
      member.inject(errors, instance, values);
    }
    stack.removeFirst();
    return errors;
  }

  protected <T> ErrorProne<T> getInstance(Key<T> type, boolean useExplicitBindings) {
    Class<? super T> rawType = type.getType().getRawType();
    // Default injections
    if (
        rawType == Injector.class
        || rawType == InternalInjector.class
    ) {
      @SuppressWarnings("unchecked")
      T value = (T) this;
      return new ErrorProne<T>(value);
    }
    ProvisionStack stack = stackForThisThread();
    // If the stack isn't empty, it's a recursive call.
    // If the key is present in the provision stack,
    // we can return the stored instance instead of creating
    // another instance
    if (stack.has(type)) {
      // It's a cyclic dependency and it's now fixed
      return new ErrorProne<T>(stack.get(type));
    }

    if (useExplicitBindings) {
      Provider<T> provider = getProviderAndInject(type);
      if (provider != null) {
        return new ErrorProne<T>(provider.get());
      }
    }

    ErrorAttachable errors = new ErrorAttachableImpl();
    InjectableConstructor constructor = membersBox.getConstructor(type.getType());
    Object instance = constructor.createInstance(errors, getValuesForKeys(constructor.getKeys(), constructor, errors));
    @SuppressWarnings("unchecked")
    T value = (T) instance;

    if (value != null) {
      injectMembers(type, value);
    }

    ErrorProne<T> errorProneValue = new ErrorProne<T>(value);
    errorProneValue.attachAll(errors);
    return errorProneValue;
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
      ErrorProne<?> errorProne = getInstance(key.getKey(), false);
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
