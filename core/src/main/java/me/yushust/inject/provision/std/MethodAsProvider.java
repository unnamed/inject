package me.yushust.inject.provision.std;

import me.yushust.inject.Provides;
import me.yushust.inject.error.BindingException;
import me.yushust.inject.error.ErrorAttachable;
import me.yushust.inject.impl.InjectorImpl;
import me.yushust.inject.impl.ProvisionStack;
import me.yushust.inject.key.Key;
import me.yushust.inject.key.TypeReference;
import me.yushust.inject.provision.StdProvider;
import me.yushust.inject.resolve.solution.InjectableMethod;
import me.yushust.inject.resolve.ComponentResolver;
import me.yushust.inject.scope.Scope;
import me.yushust.inject.scope.Scopes;

import javax.inject.Provider;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a binding to a method, the method is used like a provider,
 * passing the dependencies as parameters and getting an instance with
 * the return value
 */
public class MethodAsProvider<T>
    extends StdProvider<T> {

  private final Object moduleInstance;
  private final InjectableMethod method;
  private InjectorImpl injector;

  public MethodAsProvider(Object moduleInstance, InjectableMethod method) {
    this.moduleInstance = moduleInstance;
    this.method = method;
  }

  @Override
  public void inject(ProvisionStack stack, InjectorImpl injector) {
    this.injector = injector;
    this.injected = true;
  }

  @Override
  public T get() {
    @SuppressWarnings("unchecked")
    T value = (T) injector.getInjectionHandle().injectToMember( // supports static provider methods
        injector.stackForThisThread(), moduleInstance, method
    );
    return value;
  }

  public static <T> Map<Key<?>, Provider<?>> resolveMethodProviders(
      ErrorAttachable errors,
      TypeReference<T> type,
      T instance
  ) {

    Map<Key<?>, Provider<?>> providers = new HashMap<>();

    for (InjectableMethod injectableMethod : ComponentResolver.methods().resolve(type, Provides.class)) {
      Method method = injectableMethod.getMember();
      // TODO: Replace this shit
      Key<?> key = ComponentResolver.keys().keyOf(
          injectableMethod.getDeclaringType().resolve(method.getGenericReturnType()),
          method.getAnnotations()
      ).getKey();

      Scope scope = Scopes.getScanner().scan(method);

      Provider<?> provider = new MethodAsProvider<>(instance, injectableMethod)
          .withScope(key, scope);

      if (providers.putIfAbsent(key, provider) != null) {
        errors.attach(
            "Method provider duplicate",
            new BindingException("Type " + type + " has two or more method " +
                "providers with the same return key!")
        );
      }
    }

    return providers;
  }

}
