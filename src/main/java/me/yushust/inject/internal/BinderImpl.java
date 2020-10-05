package me.yushust.inject.internal;

import me.yushust.inject.Module;
import me.yushust.inject.ProviderMethod;
import me.yushust.inject.Qualifiers;
import me.yushust.inject.key.Key;
import me.yushust.inject.key.TypeReference;
import me.yushust.inject.resolve.InjectableMethod;
import me.yushust.inject.resolve.MembersResolver;
import me.yushust.inject.resolve.QualifierFactory;

import javax.inject.Provider;
import java.lang.reflect.Method;
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

  public void install(Iterable<? extends Module> modules) {
    for (Module module : modules) {
      // configure the manual bindings
      module.configure(this);
      List<InjectableMethod> methods = membersResolver
          .getMethods(TypeReference.of(module.getClass()));
      for (InjectableMethod injectableMethod : methods) {
        Method method = injectableMethod.getMember();
        if (!method.isAnnotationPresent(ProviderMethod.class)) {
          continue;
        }
        TypeReference<?> key = injectableMethod.getDeclaringType()
            .resolve(method.getGenericReturnType());
        bindings.put(
            Key.of(key, Qualifiers.getQualifiers(qualifierFactory, method.getAnnotations())),
            null
        );
      }
    }
  }

}
