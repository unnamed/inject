package me.yushust.inject.internal;

import me.yushust.inject.Module;
import me.yushust.inject.ProviderMethod;
import me.yushust.inject.key.Key;
import me.yushust.inject.key.TypeReference;
import me.yushust.inject.key.resolve.ContextualTypes;
import me.yushust.inject.resolve.InjectableMethod;
import me.yushust.inject.resolve.MembersBox;
import me.yushust.inject.resolve.QualifierFactory;
import me.yushust.inject.util.Annotations;

import javax.inject.Provider;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BinderImpl extends AbstractBinder {

  private final Map<Key<?>, Provider<?>> bindings =
      new HashMap<Key<?>, Provider<?>>();

  private final QualifierFactory qualifierFactory;
  private final MembersBox membersBox;

  public BinderImpl(QualifierFactory qualifierFactory, MembersBox membersBox) {
    this.qualifierFactory = qualifierFactory;
    this.membersBox = membersBox;
  }

  protected <T> void bindTo(Key<T> key, Provider<? extends T> provider) {
    provider = provider instanceof InjectedProvider
        ? provider
        : new InjectedProvider<T>(false, provider);
    this.bindings.put(key, provider);
  }

  @SuppressWarnings("unchecked")
  protected <T> InjectedProvider<? extends T> getProvider(Key<T> key) {
    // it's safe, the providers are setted
    // after (provider -> injected provider) conversion
    return (InjectedProvider<? extends T>) this.bindings.get(key);
  }

  public <T> Qualified<T> bind(TypeReference<T> keyType) {
    return new BindingBuilderImpl<T>(qualifierFactory, this, keyType);
  }

  public void install(Iterable<? extends Module> modules) {
    for (Module module : modules) {
      // configure the manual bindings
      module.configure(this);
      List<InjectableMethod> methods = membersBox
          .getMethods(TypeReference.of(module.getClass()));
      for (InjectableMethod injectableMethod : methods) {
        Method method = injectableMethod.getMember();
        if (!method.isAnnotationPresent(ProviderMethod.class)) {
          continue;
        }
        TypeReference<?> key = TypeReference.of(
            ContextualTypes.resolveContextually(
                injectableMethod.getDeclaringType(),
                method.getGenericReturnType()
            )
        );
        bindings.put(
            Key.of(key, Annotations.getQualifiers(qualifierFactory, method.getAnnotations())),
            null
        );
      }
    }
  }

}
