package me.yushust.inject.provision;

import me.yushust.inject.internal.BinderImpl;
import me.yushust.inject.internal.InternalInjector;
import me.yushust.inject.internal.ProvisionStack;
import me.yushust.inject.key.Key;
import me.yushust.inject.provision.ioc.BindListener;
import me.yushust.inject.provision.ioc.InjectionListener;
import me.yushust.inject.provision.ioc.ScopeListener;
import me.yushust.inject.scope.Scope;
import me.yushust.inject.util.Validate;

import javax.inject.Provider;
import java.util.Objects;

/**
 * Provider wrapper used for user-provided providers
 * (lowest level of library usage). Providers should
 * be wrapped because we need to store the 'injected'
 * state in providers (providers should be injected
 * only once)
 * @param <T> The provider return type
 */
public class DelegatingStdProvider<T>
    extends StdProvider<T>
    implements InjectionListener, ScopeListener<T>, BindListener {

  private final Provider<T> delegate;

  public DelegatingStdProvider(Provider<T> delegate) {
    this.delegate = Validate.notNull(delegate, "delegate");
  }

  public DelegatingStdProvider(boolean injected, Provider<T> delegate) {
    this(delegate);
    this.setInjected(injected);
  }

  public Provider<T> getDelegate() {
    return delegate;
  }

  @Override
  public void onInject(ProvisionStack stack, InternalInjector injector) {
    Providers.inject(injector, stack, delegate);
  }

  @Override
  public boolean onBind(BinderImpl binder, Key<?> key) {
    return Providers.onBind(binder, key, delegate);
  }

  @Override
  public Provider<T> withScope(Scope scope) {
    return Providers.scope(delegate, scope);
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
    if (!(o instanceof DelegatingStdProvider)) return false;
    DelegatingStdProvider<?> that = (DelegatingStdProvider<?>) o;
    return (that.isInjected() == isInjected())
        && Objects.equals(delegate, that.delegate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(isInjected(), delegate);
  }

}
