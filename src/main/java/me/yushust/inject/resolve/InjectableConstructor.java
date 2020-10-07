package me.yushust.inject.resolve;

import me.yushust.inject.error.ErrorAttachable;
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
public class InjectableConstructor {

  private final List<OptionalDefinedKey<?>> keys;
  private final Constructor<?> constructor;

  public InjectableConstructor(
      List<OptionalDefinedKey<?>> keys,
      Constructor<?> constructor
  ) {
    this.keys = Collections.unmodifiableList(keys);
    this.constructor = constructor;

    for (OptionalDefinedKey<?> key : keys) {
      Validate.doesntRequiresContext(key.getKey());
    }
    if (constructor != null) {
      this.constructor.setAccessible(true);
    }
  }

  public List<OptionalDefinedKey<?>> getKeys() {
    return keys;
  }

  public Object createInstance(ErrorAttachable errors, Object[] values) {

    Validate.argument(
        values.length == constructor.getParameterTypes().length,
        "Invalid parameter count"
    );

    try {
      return constructor.newInstance(values);
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
      errors.attach("Errors while constructing " + ElementFormatter.formatConstructor(constructor, keys), e);
    }
    return null;
  }

  @Override
  public String toString() {
    return "Constructor (" + constructor.getParameterTypes().length + " parameters)";
  }
}
