package me.yushust.inject.internal;

import me.yushust.inject.Binder;
import me.yushust.inject.key.Key;
import me.yushust.inject.key.Qualifier;
import me.yushust.inject.key.TypeReference;
import me.yushust.inject.resolve.QualifierFactory;
import me.yushust.inject.scope.Scope;
import me.yushust.inject.util.Validate;

import javax.inject.Provider;
import java.util.HashSet;
import java.util.Set;

public class BindingBuilderImpl<T> extends AbstractQualifiedBindingBuilder<T> {

  private final BinderImpl binder;
  private Key<T> key;

  protected BindingBuilderImpl(QualifierFactory factory, BinderImpl binder, TypeReference<T> key) {
    super(factory);
    this.binder = binder;
    this.key = Key.of(key);
  }

  public void in(Scope scope) {
    Validate.notNull(scope, "scope");
    selfBindingIfNotBound();
    binder.bindTo(
        key,
        scope.scope(binder.getProvider(key))
    );
  }

  public Binder.Scoped to(TypeReference<? extends T> targetType) {
    return this;
  }

  public Binder.Scoped toProvider(Provider<? extends T> provider) {
    requireNotBound();
    binder.bindTo(key, provider);
    return this;
  }

  public <P extends Provider<? extends T>> Binder.Scoped toProvider(TypeReference<P> providerClass) {
    return toProvider(Providers.providerTypeProvider(providerClass));
  }

  public void toInstance(T instance) {
    toProvider(Providers.instanceProvider(key, instance));
  }

  protected void qualified(Qualifier qualifier) {
    Validate.notNull(qualifier, "qualifier");
    Set<Qualifier> qualifiers = new HashSet<Qualifier>(
        key.getQualifiers()
    );
    qualifiers.add(qualifier);
    key = new Key<T>(key.getType(), qualifiers);
  }

  private void requireNotBound() {
    if (binder.getProvider(key) != null) {
      throw new IllegalStateException("The key is already bound");
    }
  }

  private void selfBindingIfNotBound() {
    if (binder.getProvider(key) == null) {
      toProvider(Providers.selfReferredProvider(key));
    }
  }

}
