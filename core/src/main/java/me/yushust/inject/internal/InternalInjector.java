package me.yushust.inject.internal;

import me.yushust.inject.Injector;
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
public abstract class InternalInjector implements Injector {

  // The provision stack is a sensible part
  // of the library, you must take care while
  // handling a ProvisionStack, you can cause
  // a StackOverflowError if the stack is
  // accidentally removed from the thread
  protected final ThreadLocal<ProvisionStack> provisionStackThreadLocal =
      new ThreadLocal<>();

  /**
   * Delegates the functionality to the overloaded method
   * {@link Injector#getInstance(TypeReference)} passing a
   * raw-TypeReference
   */
  public <T> T getInstance(Class<T> type) {
    return getInstance(TypeReference.of(type));
  }

  /**
   * Invokes the overloaded method
   * {@link InternalInjector#getInstance(ProvisionStack, Key, boolean)}
   * passing an empty provision stack
   */
  public <T> T getInstance(TypeReference<T> type) {
    boolean stackWasNotPresent = provisionStackThreadLocal.get() == null;
    // The creation of a new provision stack indicates
    // the manual call of getInstance() or injectMembers(),
    // the type cannot be a key. Keys are used for injectable
    // members, not for manually call a inject method
    T value = getInstance(stackForThisThread(), Key.of(type), true);
    // We need to clear the stack
    // after a manual injection,
    // the stack is only cleared if initially
    // it was null, if not, it is possibly
    // being used
    if (stackWasNotPresent) {
      removeStackFromThisThread();
    }
    return value;
  }

  /**
   * Delegates the functionality to {@link Injector#injectMembers(TypeReference, Object)}
   */
  public void injectMembers(Object object) {
    injectMembers(TypeReference.of(object.getClass()), object);
  }

  /**
   * Delegates the functionality to the abstract method
   * {@link InternalInjector#injectMembers(ProvisionStack, Key, Object)}
   * passing an empty provision stack
   */
  public <T> void injectMembers(TypeReference<T> type, T instance) {
    boolean stackWasNotPresent = provisionStackThreadLocal.get() == null;
    // The creation of a new provision stack indicates
    // the manual call of getInstance() or injectMembers(),
    // the type cannot be a key. Keys are used for injectable
    // members, not for manually call a inject method
    injectMembers(stackForThisThread(), Key.of(type), instance);
    // We need to clear the stack
    // after a manual injection,
    // the stack is only cleared if initially
    // it was null, if not, it is possibly
    // being used
    if (stackWasNotPresent) {
      removeStackFromThisThread();
    }
  }

  /**
   * @return The provision stack in the thread local, if
   * not present, creates a provision stack and stores
   * it in the thread local
   */
  @ThreadSensitive
  public ProvisionStack stackForThisThread() {
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
    ProvisionStack stack = provisionStackThreadLocal.get();
    provisionStackThreadLocal.set(null);
    if (stack != null && stack.hasErrors()) {
      throw new InjectionException(stack.formatMessages());
    }
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
  public abstract <T> void injectMembers(ProvisionStack stack, Key<T> type, T instance);

  @ThreadSensitive
  public <T> T getInstance(Key<T> type, boolean useExplicitBindings) {
    return getInstance(
        stackForThisThread(),
        type,
        useExplicitBindings
    );
  }

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
  public abstract <T> T getInstance(ProvisionStack stack,
                                       Key<T> type,
                                       boolean useExplicitBindings);

}
