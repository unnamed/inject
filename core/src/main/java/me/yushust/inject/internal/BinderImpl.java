package me.yushust.inject.internal;

import me.yushust.inject.Binder;
import me.yushust.inject.Module;
import me.yushust.inject.Provides;
import me.yushust.inject.Qualifiers;
import me.yushust.inject.error.BindingException;
import me.yushust.inject.error.ErrorAttachableImpl;
import me.yushust.inject.key.Key;
import me.yushust.inject.key.TypeReference;
import me.yushust.inject.multibinding.MultiBindingBuilderImpl;
import me.yushust.inject.provision.Providers;
import me.yushust.inject.provision.StdProvider;
import me.yushust.inject.provision.ioc.BindListener;
import me.yushust.inject.resolve.InjectableMethod;
import me.yushust.inject.resolve.MembersResolver;
import me.yushust.inject.scope.Scope;
import me.yushust.inject.scope.Scopes;
import me.yushust.inject.util.Validate;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BinderImpl extends ErrorAttachableImpl implements Binder {

  private final Map<Key<?>, Provider<?>> bindings =
      new HashMap<>();

  private final MembersResolver membersResolver;

  public BinderImpl(MembersResolver membersResolver) {
    this.membersResolver = membersResolver;
  }

  <T> void bindTo(Key<T> key, Provider<? extends T> provider) {
    if (provider instanceof BindListener) {
      ((BindListener) provider).onBind(this, key);
    }
    this.bindings.put(key, Providers.normalize(provider));
  }

  public <T> StdProvider<? extends T> getProvider(Key<T> key) {
    // it's safe, the providers are setted
    // after (provider -> injected provider) conversion
    @SuppressWarnings("unchecked")
    StdProvider<? extends T> provider =
        (StdProvider<? extends T>) this.bindings.get(key);
    return provider;
  }

  @Override
  public void $unsafeBind(Key<?> key, Provider<?> provider) {
    Validate.notNull(key, "key");
    Validate.notNull(provider, "provider");
    this.bindings.put(key, Providers.normalize(provider));
  }

  @Override
  public <T> QualifiedBindingBuilder<T> bind(TypeReference<T> keyType) {
    return new BindingBuilderImpl<>(this, keyType);
  }

  @Override
  public <T> Binder.MultiBindingBuilder<T> multibind(TypeReference<T> keyType) {
    return new MultiBindingBuilderImpl<>(this, keyType);
  }

  /** Throws the errors attached to this attachable */
  @Override
  public void reportAttachedErrors() {
    if (!hasErrors()) {
      return;
    }
    throw new BindingException(formatMessages());
  }

  public MembersResolver getResolver() {
    return membersResolver;
  }

  @Override
  public void install(Iterable<? extends Module> modules) {
    for (Module module : modules) {
      // configure the manual bindings
      module.configure(this);

      // resolve the provider methods
      List<InjectableMethod> methods = membersResolver
          .getMethods(TypeReference.of(module.getClass()), Provides.class);
      for (InjectableMethod injectableMethod : methods) {
        Method method = injectableMethod.getMember();
        TypeReference<?> key = injectableMethod.getDeclaringType()
            .resolve(method.getGenericReturnType());
        Scope scope = method.isAnnotationPresent(Singleton.class)
            ? Scopes.SINGLETON : Scopes.NONE;

        bindTo(
            Key.of(key, Qualifiers.getQualifiers(method.getAnnotations())),
            scope.scope(
                new MethodAsProvider<>(
                    Modifier.isStatic(method.getModifiers())
                        ? null
                        : module,
                    injectableMethod
                )
            )
        );
      }
    }
  }

  /**
   * Represents a binding to a method, the method is used like a provider,
   * passing the dependencies as parameters and getting an instance with
   * the return value
   */
  private static class MethodAsProvider<T> implements Provider<T> {

    private final Object moduleInstance;
    private final InjectableMethod method;
    @Inject private InjectorImpl injector;

    private MethodAsProvider(Object moduleInstance, InjectableMethod method) {
      this.moduleInstance = moduleInstance;
      this.method = method;
    }

    @Override
    public T get() {
      @SuppressWarnings("unchecked")
      T value = (T) injector.injectToMember( // supports static provider methods
          injector.stackForThisThread(), moduleInstance, method
      );
      return value;
    }
  }

}
