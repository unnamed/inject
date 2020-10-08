package me.yushust.inject.internal;

import me.yushust.inject.Module;
import me.yushust.inject.Provides;
import me.yushust.inject.Qualifiers;
import me.yushust.inject.key.Key;
import me.yushust.inject.key.TypeReference;
import me.yushust.inject.resolve.InjectableMethod;
import me.yushust.inject.resolve.MembersResolver;
import me.yushust.inject.resolve.QualifierFactory;

import javax.inject.Inject;
import javax.inject.Provider;
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

  protected <T> void bindTo(Key<T> key, Provider<? extends T> provider) {
    provider = provider instanceof InjectedProvider
        ? provider
        : new InjectedProvider<T>(false, provider);
    this.bindings.put(key, provider);
  }

  protected <T> InjectedProvider<? extends T> getProvider(Key<T> key) {
    // it's safe, the providers are setted
    // after (provider -> injected provider) conversion
    @SuppressWarnings("unchecked")
    InjectedProvider<? extends T> provider =
        (InjectedProvider<? extends T>) this.bindings.get(key);
    return provider;
  }

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
        bindTo(
            Key.of(key, Qualifiers.getQualifiers(qualifierFactory, method.getAnnotations())),
            new MethodAsProvider<>(
                Modifier.isStatic(method.getModifiers())
                    ? null
                    : module,
                injectableMethod
            )
        );
      }
    }
  }

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
