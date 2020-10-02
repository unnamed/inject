package me.yushust.inject.resolve;

import me.yushust.inject.error.ErrorAttachable;
import me.yushust.inject.util.Validate;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;

public class InjectableConstructor {

  public static final InjectableConstructor DUMMY = new InjectableConstructor(
      Collections.<OptionalDefinedKey<?>>emptyList(),
      null
  );

  private final List<OptionalDefinedKey<?>> keys;
  private final Constructor<?> constructor;

  public InjectableConstructor(
      List<OptionalDefinedKey<?>> keys,
      Constructor<?> constructor
  ) {
    this.keys = Collections.unmodifiableList(keys);
    this.constructor = constructor;
  }

  public List<OptionalDefinedKey<?>> getKeys() {
    return keys;
  }

  public Object createInstance(ErrorAttachable errors, Object[] values) {

    if (constructor == null) {
      return null;
    }

    Validate.argument(
        values.length == constructor.getParameterTypes().length,
        "Invalid parameter count"
    );

    try {
      return constructor.newInstance(values);
    } catch (InstantiationException e) {
      errors.attach(e);
    } catch (IllegalAccessException e) {
      errors.attach(e);
    } catch (InvocationTargetException e) {
      errors.attach(e);
    }
    return null;
  }

}
