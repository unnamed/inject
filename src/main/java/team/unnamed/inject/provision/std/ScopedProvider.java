package team.unnamed.inject.provision.std;

import team.unnamed.inject.Provider;
import team.unnamed.inject.impl.InjectorImpl;
import team.unnamed.inject.impl.ProvisionStack;
import team.unnamed.inject.key.Key;
import team.unnamed.inject.provision.Providers;
import team.unnamed.inject.provision.StdProvider;
import team.unnamed.inject.scope.Scope;
import team.unnamed.inject.util.Validate;

/**
 * It's a provider wrapped. Maintains the
 * unscoped provider, the scoped provider
 * and the scope.
 *
 * <p>The providers cannot be re-scoped</p>
 *
 * @param <T> The provider return type
 */
public class ScopedProvider<T>
        extends StdProvider<T>
        implements Provider<T> {

    private final Provider<T> unscoped;
    private final Provider<T> scoped;
    private final Scope scope;

    public ScopedProvider(Provider<T> provider, Scope scope) {
        this.unscoped = Validate.notNull(provider, "provider");
        this.scope = Validate.notNull(scope, "scope");
        this.scoped = scope.scope(provider);
    }

    protected ScopedProvider() {
        this.unscoped = null;
        this.scoped = null;
        this.scope = null;
    }

    @Override
    public Provider<T> withScope(Key<?> match, Scope scope) {
        if (this.scope == scope) {
            return this;
        }
        throw new UnsupportedOperationException(
                "Cannot scope the provider again! Scope: " + scope.getClass().getSimpleName()
                        + ". Provider: " + unscoped
        );
    }

    @Override
    public void inject(ProvisionStack stack, InjectorImpl injector) {
        Providers.inject(stack, injector, unscoped);
        Providers.inject(stack, injector, scoped);
        injected = true;
    }

    @Override
    public T get() {
        return scoped.get();
    }

    public Provider<T> getUnscoped() {
        return unscoped;
    }

    public Provider<T> getScoped() {
        return scoped;
    }

    public Scope getScope() {
        return scope;
    }

    public boolean requiresJitScoping() {
        return false;
    }

}
