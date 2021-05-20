package me.yushust.inject.resolve.solution;

import me.yushust.inject.impl.InjectorImpl;
import me.yushust.inject.impl.ProvisionStack;
import me.yushust.inject.util.ElementFormatter;
import me.yushust.inject.key.InjectedKey;
import me.yushust.inject.key.TypeReference;
import me.yushust.inject.util.Validate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;

/**
 * Represents a Method annotated with {@link javax.inject.Inject}
 * and that already has resolved its parameter keys, with its
 * requirement level defined too.
 */
public class InjectableMethod implements InjectableMember {

  private final TypeReference<?> declaringType;
  private final List<InjectedKey<?>> keys;
  private final Method method;

  public InjectableMethod(
      TypeReference<?> declaringType,
      List<InjectedKey<?>> keys,
      Method method
  ) {
    this.declaringType = Validate.notNull(declaringType);
    this.keys = Collections.unmodifiableList(keys);
    this.method = Validate.notNull(method);

    for (InjectedKey<?> key : keys) {
      Validate.doesntRequiresContext(key.getKey());
    }
    this.method.setAccessible(true);
  }

  @Override
  public TypeReference<?> getDeclaringType() {
    return declaringType;
  }

  @Override
  public Method getMember() {
    return method;
  }

  @Override
  public Object inject(InjectorImpl injector, ProvisionStack stack, Object target) {

    if (target == null ^ Modifier.isStatic(method.getModifiers())) {
      return null;
    }

    Object[] values = new Object[keys.size()];

    for (int i = 0; i < keys.size(); i++) {
      InjectedKey<?> key = keys.get(i);
      Object value = injector.getValue(key, stack);

      if (value == InjectorImpl.ABSENT_INSTANCE) {
        stack.attach(
            "Cannot inject '" + method.getName() + "' method."
                + "\n\tAt:" + declaringType
                + "\n\tReason: Cannot get value for required parameter (index " + i + ")"
                +" \n\tRequired Key: " + key.getKey()
        );
        return null;
      }

      values[i] = value;
    }

    try {
      return method.invoke(target, values);
    } catch (IllegalAccessException | InvocationTargetException e) {
      stack.attach(
          "Cannot inject method "
              + ElementFormatter.formatMethod(method, keys),
          e
      );
      return null;
    }
  }
}
