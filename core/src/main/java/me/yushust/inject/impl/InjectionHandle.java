package me.yushust.inject.impl;

import me.yushust.inject.key.InjectedKey;

import java.util.List;

public class InjectionHandle {

  public static final Object ERRORED_RESULT = new Object();

  public static Object getValue(
      InjectedKey<?> key,
      InjectorImpl injector,
      ProvisionStack stack
  ) {
    // We don't need to clone the stack,
    // the type-instance relations are
    // removes automatically when ended
    // with the injection
    List<String> snapshot = stack.getErrorMessages();
    Object value = injector.getInstance(stack, key.getKey(), true);
    if (value == null && !key.isOptional()) {
      return ERRORED_RESULT;
    } else if (key.isOptional()) {
      // remove errors because the injection
      // is optional and we don't need a report
      // of fails that can be valid
      stack.applySnapshot(snapshot);
    }
    return value;
  }

}
