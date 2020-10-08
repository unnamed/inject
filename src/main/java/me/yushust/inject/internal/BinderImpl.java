package me.yushust.inject.internal;

import me.yushust.inject.Module;
import me.yushust.inject.Provides;
import me.yushust.inject.Qualifiers;
import me.yushust.inject.key.Key;
import me.yushust.inject.key.TypeReference;
import me.yushust.inject.resolve.InjectableMethod;
import me.yushust.inject.resolve.MembersResolver;
import me.yushust.inject.resolve.QualifierFactory;
import me.yushust.inject.scope.Scope;
import me.yushust.inject.scope.Scopes;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BinderImpl extends AbstractBinder {

  private final Map<Key<?>, Provider<?>> bindings =
      new HashMap<>();

  private final QualifierFactory qualifierFactory;
  private final MembersResolver membersResolver;

  public BinderImpl(QualifierFactory qualifierFactory, MembersResolver membersResolver) {
    this.qualifierFactory = qualifierFactory;
    this.membersResolver = membersResolver;
  }

  <T> void bindTo(Key<T> key, Provider<? extends T> provider) {
    this.bindings.put(key, injected(provider));
  }

  <T> InjectedProvider<? extends T> getProvider(Key<T> key) {
    // it's safe, the providers are setted
    // after (provider -> injected provider) conversion
    @SuppressWarnings("unchecked")
    InjectedProvider<? extends T> provider =
        (InjectedProvider<? extends T>) this.bindings.get(key);
    return provider;
  }

  <T> InjectedProvider<T> injected(Provider<T> provider) {
    return provider instanceof InjectedProvider
        ? (InjectedProvider<T>) provider
        : new InjectedProvider<T>(false, provider);
  }

  @Override
  public <T> Qualified<T> bind(TypeReference<T> keyType) {
    return new BindingBuilderImpl<>(qualifierFactory, this, keyType);
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
            Key.of(key, Qualifiers.getQualifiers(qualifierFactory, method.getAnnotations())),
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
