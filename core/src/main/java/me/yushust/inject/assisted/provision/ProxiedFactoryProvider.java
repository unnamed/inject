package me.yushust.inject.assisted.provision;

import me.yushust.inject.assisted.ValueFactory;
import me.yushust.inject.impl.InjectorImpl;
import me.yushust.inject.impl.ProvisionStack;
import me.yushust.inject.key.InjectedKey;
import me.yushust.inject.key.Key;
import me.yushust.inject.key.TypeReference;
import me.yushust.inject.provision.StdProvider;
import me.yushust.inject.resolve.solution.InjectableConstructor;
import me.yushust.inject.util.ElementFormatter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProxiedFactoryProvider<T>
        extends StdProvider<T> {

    private final Class<? extends ValueFactory> factory;
    private final Method method;
    private final List<InjectedKey<?>> keys;
    private final InjectableConstructor constructor;
    private final Key<?> key;
    private T factoryInstance;

    ProxiedFactoryProvider(
            Class<? extends ValueFactory> factory,
            Method method,
            List<InjectedKey<?>> keys,
            InjectableConstructor constructor,
            Key<?> key
    ) {
        this.factory = factory;
        this.method = method;
        this.keys = keys;
        this.constructor = constructor;
        this.key = key;
    }

    public Class<? extends ValueFactory> getFactory() {
        return factory;
    }

    public Method getFactoryMethod() {
        return method;
    }

    public Key<?> getBuildType() {
        return key;
    }

    public Constructor<?> getTargetConstructor() {
        return constructor.getMember();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Object createInstance(InjectorImpl injector, Object[] extras) {

        Map<Key<?>, Object> values = new HashMap<>();
        for (int i = 0; i < extras.length; i++) {
            Key<?> valueKey = keys.get(i).getKey();
            Object value = extras[i];
            values.put(valueKey, value);
        }

        Object[] givenArgs = new Object[constructor.getKeys().size()];

        int i = 0;
        for (InjectedKey<?> injection : constructor.getKeys()) {
            if (injection.isAssisted()) {
                givenArgs[i] = values.get(injection.getKey());
            } else {
                Object val = injector.getInstance(
                        injector.stackForThisThread(),
                        injection.getKey(),
                        true
                );
                givenArgs[i] = val;
            }
            i++;
        }


        try {
            Object instance = constructor.getMember().newInstance(givenArgs);
            injector.injectMembers(
                    (TypeReference) key.getType(),
                    instance
            );
            return instance;
        } catch (
                InstantiationException
                        | InvocationTargetException
                        | IllegalAccessException e
        ) {
            injector.stackForThisThread().attach(
                    "Errors while invoking assisted constructor "
                            + ElementFormatter.formatConstructor(constructor.getMember(), keys),
                    e
            );
            return null;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void inject(ProvisionStack stack, InjectorImpl injector) {
        factoryInstance = (T) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[]{factory},
                (proxy, method, args) -> {
                    if (method.equals(this.method)) {
                        return createInstance(injector, args);
                    } else {
                        switch (method.getName()) {
                            case "equals":
                                return false;
                            case "hashCode":
                                return 0;
                            case "toString":
                                return factory.getName() + " Trew-generated implementation";
                            default:
                                return null;
                        }
                    }
                }
        );
        injected = true;
    }

    @Override
    public T get() {
        return factoryInstance;
    }

}
