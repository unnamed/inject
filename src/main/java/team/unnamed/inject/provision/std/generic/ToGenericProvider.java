package team.unnamed.inject.provision.std.generic;

import team.unnamed.inject.impl.BinderImpl;
import team.unnamed.inject.impl.InjectorImpl;
import team.unnamed.inject.impl.ProvisionStack;
import team.unnamed.inject.key.Key;
import team.unnamed.inject.provision.StdProvider;
import team.unnamed.inject.provision.std.ScopedProvider;
import team.unnamed.inject.scope.Scope;
import team.unnamed.inject.util.Validate;

import javax.inject.Provider;

public class ToGenericProvider<T>
        extends ScopedProvider<T>
        implements Provider<T> {

    private final GenericProvider<T> provider;
    private Scope scope;

    public ToGenericProvider(GenericProvider<T> provider) {
        this.provider = Validate.notNull(provider, "provider");
    }

    @Override
    public void inject(ProvisionStack stack, InjectorImpl injector) {
        // don't inject null references
        injected = true;
    }

    @Override
    public boolean onBind(BinderImpl binder, Key<?> key) {

        boolean isRawType = key.isPureRawType();

        if (!isRawType) {
            binder.attach("You must bound the raw-type to a GenericProvider, " +
                    "not a parameterized type! (key: " + key + ", genericProvider: " + provider + ")");
        }

        return isRawType;
    }

    @Override
    public T get() {
        throw new IllegalStateException("Key was bound to a generic provider," +
                " it cannot complete a raw-type!\n\tProvider: " + provider);
    }

    /**
     * Special injector case for keys bound
     * to generic providers
     */
    @Override
    public T get(Key<?> bound) {
        return provider.get(bound);
    }

    @Override
    public Provider<T> withScope(Key<?> match, Scope scope) {
        if (scope != null) {
            this.scope = scope;
        }
        if (match.isPureRawType()) {
            return this;
        } else {
            return new SyntheticGenericProvider(
                    match,
                    scope == null ? this.scope : scope
            );
        }
    }

    @Override
    public boolean requiresJitScoping() {
        return true;
    }

    public class SyntheticGenericProvider
            extends StdProvider<T>
            implements Provider<T> {

        private final Scope scope;
        private final Provider<T> scoped;

        public SyntheticGenericProvider(Key<?> match, Scope scope) {
            this.scope = scope;
            Provider<T> unscoped = ToGenericProvider.this.provider.asConstantProvider(match);
            this.scoped = scope == null ? unscoped : scope.scope(unscoped);
            setInjected(true);
        }

        @Override
        public T get() {
            return scoped.get();
        }

        @Override
        public Provider<T> withScope(Key<?> match, Scope scope) {
            Validate.argument(this.scope == scope, "Not the same scope on GenericProvider!");
            return new SyntheticGenericProvider(match, scope);
        }

    }

}
