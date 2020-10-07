package me.yushust.inject.resolve;

import me.yushust.inject.error.ErrorAttachable;
import me.yushust.inject.key.TypeReference;
import me.yushust.inject.util.ElementFormatter;
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
  private final List<OptionalDefinedKey<?>> keys;
  private final Method method;

  public InjectableMethod(
      TypeReference<?> declaringType,
      List<OptionalDefinedKey<?>> keys,
      Method method
  ) {
    this.declaringType = Validate.notNull(declaringType);
    this.keys = Collections.unmodifiableList(keys);
    this.method = Validate.notNull(method);

    for (OptionalDefinedKey<?> key : keys) {
      Validate.doesntRequiresContext(key.getKey());
    }
    this.method.setAccessible(true);
  }

  public TypeReference<?> getDeclaringType() {
    return declaringType;
  }

  public Method getMember() {
    return method;
  }

  public List<OptionalDefinedKey<?>> getKeys() {
    return keys;
  }

  /**
   * Injects the values to the already specified
   * method in the provided {@code target}
   *
   * @throws IllegalArgumentException If the target is null and method
   *                                  isn't static, or if the target
   *                                  isn't present in the type or its supertypes;
   */
  public void inject(ErrorAttachable errors, Object target, Object[] values) {

    Validate.argument(
        target != null
            || Modifier.isStatic(method.getModifiers()),
        "Target instance is null and the method isn't static!"
    );
    Validate.argument(
        target == null
            || declaringType.getRawType().isAssignableFrom(target.getClass()),
        "Field isn't present in the target class"
    );

    try {
      method.invoke(target, values);
    } catch (IllegalAccessException | InvocationTargetException e) {
      errors.attach("Error while trying to invoke " + ElementFormatter.formatMethod(method, keys), e);
    }
  }
}
