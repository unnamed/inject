package team.unnamed.inject.impl;

import team.unnamed.inject.Binder;
import team.unnamed.inject.key.Key;
import team.unnamed.inject.key.TypeReference;
import team.unnamed.inject.provision.Providers;
import team.unnamed.inject.scope.Scope;
import team.unnamed.inject.util.Validate;

import javax.inject.Provider;

class BindingBuilderImpl<T> implements
        Binder.QualifiedBindingBuilder<T>,
        KeyBuilder<Binder.QualifiedBindingBuilder<T>, T>,
        LinkedBuilder<Binder.Scoped, T> {

    private final BinderImpl binder;
    private Key<T> key;

    protected BindingBuilderImpl(BinderImpl binder, TypeReference<T> key) {
        this.key = Key.of(key);
        this.binder = binder;
    }

    @Override
    public void in(Scope scope) {
        Validate.notNull(scope, "scope");
        selfBindingIfNotBound();
        binder.$unsafeBind(
                key,
                binder.getProvider(key)
                        .withScope(key, scope)
        );
    }

    @Override
    public Binder.Scoped toProvider(Provider<? extends T> provider) {
        Validate.notNull(provider, "provider");
        requireNotBound();
        binder.$unsafeBind(key, provider);
        return this;
    }

    @Override
    public void toInstance(T instance) {
        Validate.notNull(instance, "instance");
        toProvider(Providers.instanceProvider(key, instance));
    }

    private void requireNotBound() {
        if (binder.getProvider(key) != null) {
            throw new IllegalStateException("The key is already bound");
        }
    }

    private void selfBindingIfNotBound() {
        if (binder.getProvider(key) == null) {
            toProvider(Providers.link(key, key));
        }
    }

    @Override
    public Key<T> key() {
        return key;
    }

    @Override
    public void setKey(Key<T> key) {
        this.key = key;
    }

    @Override
    public Binder.QualifiedBindingBuilder<T> getReturnValue() {
        return this;
    }

}
