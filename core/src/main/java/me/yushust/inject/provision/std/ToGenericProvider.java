package me.yushust.inject.provision.std;

import me.yushust.inject.GenericProvider;
import me.yushust.inject.impl.BinderImpl;
import me.yushust.inject.impl.InjectorImpl;
import me.yushust.inject.impl.ProvisionStack;
import me.yushust.inject.key.Key;
import me.yushust.inject.key.TypeReference;
import me.yushust.inject.provision.StdProvider;
import me.yushust.inject.provision.ioc.BindListener;
import me.yushust.inject.provision.ioc.ScopeListener;
import me.yushust.inject.scope.Scope;
import me.yushust.inject.util.Validate;

import javax.inject.Provider;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ToGenericProvider<T>
    extends ScopedProvider<T>
    implements BindListener, ScopeListener<T> {

  private final GenericProvider<T> provider;
  private Scope scope;

  public ToGenericProvider(GenericProvider<T> provider) {
    this.provider = Validate.notNull(provider, "provider");
  }

  @Override
  public void onInject(ProvisionStack stack, InjectorImpl injector) {
    // don't inject null references
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

  @Override
  public Provider<T> withScope(Key<?> match, Scope scope) {
    if (scope != null) {
      this.scope = scope;
    }
    if (match.isPureRawType()) {
      return this;
    }
    return new SyntheticGenericProvider(
        match,
        scope == null ? this.scope : scope
    );
  }

  @Override
  public boolean requiresJitScoping() {
    return true;
  }

  /**
   * Special injector case for keys bound to generic providers
   */
  @Override
  public T get(Key<?> bound) {

    if (bound.getType().getType() == bound.getType().getRawType()) {
      get(); // throws an exception
      return null; // this never passes
    }

    Type type = bound.getType().getType();
    if (!(type instanceof ParameterizedType)) {
      throw new IllegalStateException("Matched key isn't a ParameterizedType!\n\tKey: "
          + bound + "\n\tProvider: " + provider);
    }

    ParameterizedType parameterizedType = (ParameterizedType) type;
    Type[] typeParameters = parameterizedType.getActualTypeArguments();
    TypeReference<?>[] handleableTypeParameters = new TypeReference[typeParameters.length];

    for (int i = 0; i < typeParameters.length; i++) {
      handleableTypeParameters[i] = TypeReference.of(typeParameters[i]);
    }

    return provider.get(bound.getType().getRawType(), handleableTypeParameters);
  }

  public class SyntheticGenericProvider
      extends StdProvider<T>
      implements ScopeListener<T> {

    private final Scope scope;
    private final Provider<T> scoped;

    public SyntheticGenericProvider(Key<?> match, Scope scope) {
      this.scope = scope;
      Provider<T> unscoped = () -> ToGenericProvider.this.get(match);
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
