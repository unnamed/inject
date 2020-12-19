package me.yushust.inject.scope;

import me.yushust.inject.key.Key;
import me.yushust.inject.provision.StdProvider;
import me.yushust.inject.provision.ioc.MatchListener;

import javax.inject.Provider;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A singleton provider wrapper. The implementation is
 * an enum to let the JVM make sure only one instance exists
 */
public enum SingletonScope implements Scope {

  /** The singleton instance */
  INSTANCE;

  @Override
  public <T> Provider<T> scope(Provider<T> unscoped) {

    // the provider is already scoped
    if (unscoped instanceof SingletonProvider) {
      return unscoped;
    }

    return new SingletonProvider<>(unscoped);
  }

  /**
   * Singleton provider. Singleton instance is instantiated
   * using double-checked-locking with ReentrantLock.
   * We use a volatile reference for more thread-safety
   *
   * <p>The singleton provider wrapper executes the delegated
   * provider once, then, returns the same saved instance.</p>
   *
   * <p>The singleton provider extends to {@link StdProvider}
   * only for optimization, the injection is never invoked in
   * the SingletonProvider, it's invoked in the delegate.</p>
   *
   * @param <T> The provided type
   */
  static class SingletonProvider<T>
      implements Provider<T>, MatchListener<T> {

    private final Lock instanceLock = new ReentrantLock();

    /**
     * Volatile reference to saved instance. Initially null,
     * we double-check if the instance is null
     */
    private volatile T instance;
    private final Provider<T> delegate;

    /**
     * Constructs a new Singleton Provider wrapper
     *
     * @param unscoped The unscoped provider
     */
    SingletonProvider(Provider<T> unscoped) {
      this.delegate = unscoped;
    }

    @Override
    public T get() {
      return get(null);
    }

    @Override
    public T get(Key<?> match) {

      // non-synchronized check
      if (instance == null) {
        instanceLock.lock();
        try {
          if (instance == null) { // synchronized check
            if (match != null && delegate instanceof MatchListener) {
              instance = ((MatchListener<T>) delegate).get(match);
            } else {
              instance = delegate.get();
            }
          }
        } finally { // important to release the lock in a finally block
          instanceLock.unlock();
        }
      }

      return instance;
    }

    @Override
    public String toString() {
      return "Singleton(" + delegate.toString() + ")";
    }
  }

}
