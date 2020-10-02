package me.yushust.inject.internal;

import me.yushust.inject.Binder;
import me.yushust.inject.Module;
import me.yushust.inject.error.BindingException;
import me.yushust.inject.error.ErrorAttachableImpl;
import me.yushust.inject.error.Errors;
import me.yushust.inject.key.TypeReference;
import me.yushust.inject.util.Validate;

import java.util.Arrays;
import java.util.Collections;

/**
 * Abstract class that implements {@link Binder} and
 * methods that delegates the functionality to another
 * overloaded method.
 *
 * <p>This abstract class just removes the responsibility
 * of creating method that calls another methods</p>
 */
public abstract class AbstractBinder extends ErrorAttachableImpl implements Binder {

  /** Delegates the real functionality to {@link Binder#bind(TypeReference)} */
  public <T> Qualified<T> bind(Class<T> keyType) {
    Validate.notNull(keyType, "keyType");
    return bind(TypeReference.<T>of(keyType));
  }

  /** Delegates the real functionality to {@link Binder#install(Iterable)}
   * passing a singleton list as {@code Iterable{@literal <}Module{@literal >}}*/
  public void install(Module module) {
    Validate.notNull(module, "module");
    install(Collections.singletonList(module));
  }

  /** Delegates the real functionality to {@link Binder#install(Iterable)}
   * passing an {@code ArrayList{@literal <}Module{@literal >}}*/
  public void install(Module... modules) {
    install(Arrays.asList(modules));
  }

  /** Throws the errors attached to this attachable */
  public void reportAttachedErrors() {
    if (!hasErrors()) {
      throw new IllegalStateException("The attachable doesn't contain errors!");
    }
    throw new BindingException(Errors.formatErrorMessages(getErrorMessages()));
  }

}
