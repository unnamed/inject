package me.yushust.inject.internal;

import me.yushust.inject.key.Key;
import me.yushust.inject.key.TypeReference;
import me.yushust.inject.scope.Scope;
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
public class InjectedProvider<T> implements Provider<T> {

  protected final Provider<T> delegate;
  private boolean injected;

  public InjectedProvider(boolean injected, Provider<T> delegate) {
    this.injected = injected;
    this.delegate = Validate.notNull(delegate, "delegate");
  }

  public InjectedProvider(Provider<T> delegate) {
    this(false, delegate);
  }

  public void inject(ProvisionStack stack, InternalInjector injector) {
    if (this.injected) {
      return;
    }
    if (delegate instanceof InjectedProvider) {
      ((InjectedProvider<?>) delegate).inject(stack, injector);
    } else {
      injector.injectMembers(stack, Key.of(TypeReference.of(delegate.getClass())), delegate);
    }
    this.injected = true;
  }

  public InjectedProvider<T> withScope(Scope scope) {
    return new InjectedProvider<>(injected, scope.scope(delegate));
  }

  public Provider<T> getDelegate() {
    return delegate instanceof InjectedProvider ?
        ((InjectedProvider<T>) delegate).getDelegate()
        : delegate;
  }

  public boolean isInjected() {
    return this.injected;
  }

  public void setInjected(boolean injected) {
    this.injected = injected;
  }

  @Override
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
