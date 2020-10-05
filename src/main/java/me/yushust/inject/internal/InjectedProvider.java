package me.yushust.inject.internal;

import me.yushust.inject.util.Validate;

import javax.inject.Provider;

/**
 * Wrapper for {@link Provider} that adds a "injected" state,
 * a provider injection must be executed only once, then just
 * use the {@link Provider#get()} method to get the instances
 *
 * <p>The added state helps us with this, with this state the
 * injector can know if the provider is already injected or not</p>
 *
 * <p>This class is handled by the injector binder</p>
 */
class InjectedProvider<T> implements Provider<T> {

  private final Provider<? extends T> delegate;
  private boolean injected;

  public InjectedProvider(boolean injected, Provider<? extends T> delegate) {
    this.injected = injected;
    this.delegate = Validate.notNull(delegate);
  }

  public Provider<? extends T> getDelegate() {
    return delegate;
  }

  public boolean isInjected() {
    return this.injected;
  }

  public void setInjected(boolean injected) {
    this.injected = injected;
  }

  public T get() {
    return delegate.get();
  }

  @Override
  public String toString() {
    return delegate.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    InjectedProvider<?> that = (InjectedProvider<?>) o;
    return injected == that.injected &&
        delegate.equals(that.delegate);
  }

  @Override
  public int hashCode() {
    int result = 1;
    result = 31 * result + (injected ? 1 : 0);
    result = 31 * result + delegate.hashCode();
    return result;
  }

}
