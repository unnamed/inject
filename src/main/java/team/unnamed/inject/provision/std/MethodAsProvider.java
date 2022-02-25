package team.unnamed.inject.provision.std;

import team.unnamed.inject.Provides;
import team.unnamed.inject.error.BindingException;
import team.unnamed.inject.error.ErrorAttachable;
import team.unnamed.inject.impl.InjectorImpl;
import team.unnamed.inject.impl.ProvisionStack;
import team.unnamed.inject.key.Key;
import team.unnamed.inject.key.TypeReference;
import team.unnamed.inject.provision.StdProvider;
import team.unnamed.inject.resolve.ComponentResolver;
import team.unnamed.inject.resolve.solution.InjectableMethod;
import team.unnamed.inject.scope.Scope;
import team.unnamed.inject.scope.Scopes;

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

    @Override
    public void inject(ProvisionStack stack, InjectorImpl injector) {
        this.injector = injector;
        this.injected = true;
    }

    @Override
    public T get() {
        @SuppressWarnings("unchecked")
        T value = (T) method.inject(injector, injector.stackForThisThread(), moduleInstance);
        return value;
    }

}
