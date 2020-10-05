package me.yushust.inject.internal;

import me.yushust.inject.Injector;
import me.yushust.inject.error.ErrorAttachable;
import me.yushust.inject.error.ErrorProne;
import me.yushust.inject.error.InjectionException;
import me.yushust.inject.key.Key;
import me.yushust.inject.key.TypeReference;

/**
 * Abstract class that implements {@link Injector} and
 * methods that delegates the functionality to another
 * overloaded method.
 *
 * <p>This abstract class removes the responsibility
 * of creating method that calls another methods</p>
 *
 * <p>This abstract class also adds methods for internal
 * usage</p>
 */
abstract class InternalInjector implements Injector {

  // The provision stack is a sensible part
  // of the library, you must take care while
  // handling a ProvisionStack, you can cause
  // a StackOverflowError if the stack is
  // accidentally removed from the thread
  private final ThreadLocal<ProvisionStack> provisionStackThreadLocal =
      new ThreadLocal<>();
  private boolean debug;

  /**
   * @return True if the debug is enabled in this injector
   */
  public boolean isDebugEnabled() {
    return debug;
  }

  /**
   * Toggles the debug mode. {@code debug = !debug}
   */
  public Injector toggleDebug() {
    debug = !debug;
    return this;
  }

  /**
   * Delegates the functionality to the overloaded method
   * {@link Injector#getInstance(TypeReference)} passing a
   * raw-TypeReference
   */
  @ExternalUseOnly
  public <T> T getInstance(Class<T> type) {
    return getInstance(TypeReference.of(type));
  }

  /**
   * Invokes the overloaded method
   * {@link InternalInjector#getInstance(Key, boolean)}
   * passing an empty provision stack
   */
  @ExternalUseOnly
  public <T> T getInstance(TypeReference<T> type) {
    ProvisionStack stack = provisionStackThreadLocal.get();
    // The creation of a new provision stack indicates
    // the manual call of getInstance() or injectMembers(),
    // the type cannot be a key. Keys are used for injectable
    // members, not for manually call a inject method
    ErrorProne<T> errorProne = getInstance(Key.of(type), true);
    if (errorProne.hasErrors()) {
      throw new InjectionException(errorProne.formatMessages());
    }
    // We need to clear the stack
    // after a manual injection,
    // the stack is only cleared if initially
    // it was null, if not, it is possibly
    // being used
    if (stack == null) {
      removeStackFromThisThread();
    }
    return errorProne.getValue();
  }

  /**
   * Delegates the functionality to {@link Injector#injectMembers(TypeReference, Object)}
   */
  @ExternalUseOnly
  public void injectMembers(Object object) {
    injectMembers(TypeReference.of(object.getClass()), object);
  }

  /**
   * Delegates the functionality to the abstract method
   * {@link InternalInjector#injectMembers(Key, Object)}
   * passing an empty provision stack
   */
  @ExternalUseOnly
  public <T> void injectMembers(TypeReference<T> type, T instance) {
    ProvisionStack stack = provisionStackThreadLocal.get();
    // The creation of a new provision stack indicates
    // the manual call of getInstance() or injectMembers(),
    // the type cannot be a key. Keys are used for injectable
    // members, not for manually call a inject method
    injectMembers(Key.of(type), instance);
    // We need to clear the stack
    // after a manual injection,
    // the stack is only cleared if initially
    // it was null, if not, it is possibly
    // being used
    if (stack == null) {
      removeStackFromThisThread();
    }
  }

  /**
   * @return The provision stack in the thread local, if
   * not present, creates a provision stack and stores
   * it in the thread local
   */
  @ThreadSensitive
  protected ProvisionStack stackForThisThread() {
    ProvisionStack stack = provisionStackThreadLocal.get();
    // the stack doesn't exist, create a new stack
    // and set to the thread local
    if (stack == null) {
      stack = new ProvisionStack();
      provisionStackThreadLocal.set(stack);
    }
    return stack;
  }

  /**
   * Sets the provision stack in this thread to null,
   * so it should be re-started with {@link InternalInjector#stackForThisThread}.
   *
   * <p>Simple explanation: There's one {@link ProvisionStack}
   * per thread. This is handled using a {@link ThreadLocal}</p>
   */
  @ThreadSensitive
  protected void removeStackFromThisThread() {
    provisionStackThreadLocal.set(null);
  }

  /**
   * Injects members to the specified object. If the method isn't
   * called recursively, the {@code type} doesn't contain qualifiers.
   * If the method is called recursively, it can contain or not, qualifiers.
   *
   * @param type     The injecting type
   * @param instance The injected instance
   */
  @ThreadSensitive
  protected abstract <T> ErrorAttachable injectMembers(Key<T> type, T instance);

  /**
   * Constructs an instance of the specified type. If the method isn't
   * called recursively, the {@code type} doesn't contain qualifiers.
   * If the method is called recursively, it can contain or not, qualifiers.
   *
   * @param type                The expected instance type
   * @param useExplicitBindings The name is very descriptive, use
   *                            explicit bindings to obtain this
   *                            instance, true by default
   */
  @ThreadSensitive
  protected abstract <T> ErrorProne<T> getInstance(Key<T> type,
                                                   boolean useExplicitBindings);

}
