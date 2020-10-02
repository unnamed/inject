package me.yushust.inject.scope;

import me.yushust.inject.Injector;
import me.yushust.inject.util.Validate;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A singleton provider wrapper
 */
class SingletonScope implements Scope {

  public <T> Provider<T> scope(Provider<T> unscoped) {

    // the provider is already scoped
    if (unscoped instanceof SingletonProvider) {
      return unscoped;
    }

    return new SingletonProvider<T>(unscoped);
  }

  /**
   * Singleton provider. Singleton instance is instantiated
   * using double-checked-locking with ReentrantLock.
   * We use a volatile reference for more thread-safety
   *
   * @param <T> The provided type
   */
  static class SingletonProvider<T> implements Provider<T> {

    private final Lock instanceLock = new ReentrantLock();
    private final Provider<T> unscoped;

    /**
     * Volatile reference to saved instance. Initially null,
     * we double-check if the instance is null
     */
    private volatile T instance;

    /**
     * Constructs a new Singleton Provider wrapper
     *
     * @param unscoped The unscoped provider
     */
    SingletonProvider(Provider<T> unscoped) {
      this.unscoped = Validate.notNull(unscoped);
    }

    /**
     * Delegates members injection to the unscoped provider
     *
     * @param injector The injector
     */
    @Inject
    public void inject(Injector injector) {
      injector.injectMembers(unscoped);
    }

    /**
     * {@inheritDoc}
     */
    public T get() {

      // non-synchronized check
      if (instance == null) {
        // lock
        instanceLock.lock();
        try {
          if (instance == null) { // synchronized check
            // instantiate
            instance = unscoped.get();
          }
        } finally { // important to release the lock in a finally block
          instanceLock.unlock();
        }
      }

      // finally, return the instance
      return instance;

    }

  }

}
