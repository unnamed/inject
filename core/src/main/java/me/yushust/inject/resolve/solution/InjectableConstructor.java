package me.yushust.inject.resolve.solution;

import me.yushust.inject.impl.InjectionHandle;
import me.yushust.inject.impl.InjectorImpl;
import me.yushust.inject.impl.ProvisionStack;
import me.yushust.inject.key.TypeReference;
import me.yushust.inject.util.ElementFormatter;
import me.yushust.inject.key.InjectedKey;
import me.yushust.inject.util.Validate;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;

/**
 * Represents an injectable constructor, a constructor
 * annotated with {@link javax.inject.Inject} or a
 * constructor with no parameters.
 */
public class InjectableConstructor implements InjectableMember {

  private final List<InjectedKey<?>> keys;

  private final TypeReference<?> declaringType;
  private final Constructor<?> constructor;

  public InjectableConstructor(
      List<InjectedKey<?>> keys,
      Constructor<?> constructor
  ) {
    this.keys = Collections.unmodifiableList(keys);
    this.constructor = constructor;

    for (InjectedKey<?> key : keys) {
      Validate.doesntRequiresContext(key.getKey());
    }
    if (constructor != null) {
      this.constructor.setAccessible(true);
      this.declaringType = TypeReference.of(constructor.getDeclaringClass());
    } else {
      this.declaringType = null;
    }
  }

  @Override
  public TypeReference<?> getDeclaringType() {
    return declaringType;
  }

  @Override
  public Constructor<?> getMember() {
    return constructor;
  }

  public List<InjectedKey<?>> getKeys() {
    return keys;
  }

  @Override
  public Object inject(InjectorImpl injector, ProvisionStack stack, Object target) {

    Object[] values = new Object[keys.size()];

    for (int i = 0; i < keys.size(); i++) {
      InjectedKey<?> key = keys.get(i);
      Object value = InjectionHandle.getValue(key, injector, stack);

      if (value == InjectionHandle.ERRORED_RESULT) {
        stack.attach(
            "Cannot instantiate class"
                + "\n\tClass: " + constructor.getName()
                + "\n\tReason: Cannot get value for required parameter (index " + i + ")"
                +" \n\tRequired Key: " + key.getKey()
        );
        return null;
      } else {
        values[i] = value;
      }
    }

    try {
      return constructor.newInstance(values);
    } catch (
        InstantiationException
            | IllegalAccessException
            | InvocationTargetException e
    ) {
      stack.attach(
          "Errors while constructing "
            + ElementFormatter.formatConstructor(constructor, keys),
          e
      );
    }
    return null;
  }

}
