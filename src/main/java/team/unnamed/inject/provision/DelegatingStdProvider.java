package team.unnamed.inject.provision;

import team.unnamed.inject.Provider;
import team.unnamed.inject.impl.BinderImpl;
import team.unnamed.inject.impl.InjectorImpl;
import team.unnamed.inject.impl.ProvisionStack;
import team.unnamed.inject.key.Key;
import team.unnamed.inject.scope.Scope;
import team.unnamed.inject.util.Validate;

import java.util.Objects;

/**
 * Provider wrapper used for user-provided providers
 * (lowest level of library usage). Providers should
 * be wrapped because we need to store the 'injected'
 * state in providers (providers should be injected
 * only once)
 *
 * @param <T> The provider return type
 */
public class DelegatingStdProvider<T>
        extends StdProvider<T>
        implements Provider<T> {

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
    public void inject(ProvisionStack stack, InjectorImpl injector) {
        Providers.inject(stack, injector, delegate);
        injected = true;
    }

    @Override
    public boolean onBind(BinderImpl binder, Key<?> key) {
        if (delegate instanceof StdProvider) {
            return ((StdProvider<?>) delegate).onBind(binder, key);
        } else {
            return true;
        }
    }

    @Override
    public Provider<T> withScope(Key<?> match, Scope scope) {
        if (delegate instanceof StdProvider) {
            return ((StdProvider<T>) delegate).withScope(match, scope);
        } else {
            return super.withScope(match, scope);
        }
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
