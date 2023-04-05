package team.unnamed.inject.provision.std;

import team.unnamed.inject.Provider;
import team.unnamed.inject.key.Key;
import team.unnamed.inject.provision.StdProvider;
import team.unnamed.inject.scope.Scope;
import team.unnamed.inject.scope.Scopes;

/**
 * Represents an instance binding. The key is bound to a
 * specific instance. For example:
 *
 * <pre>
 *   public class Foo {
 *
 *     private final String name;
 *     // ...
 *   }
 * </pre>
 *
 * <p>The binding:
 * {@code bind(Foo.class).toInstance(new Foo());}
 * should work</p>
 * <p>
 * The bound instances are not injected
 */
public class InstanceProvider<T>
        extends StdProvider<T>
        implements Provider<T> {

    private final T instance;

    public InstanceProvider(T instance) {
        this.instance = instance;
        setInjected(true);
    }

    @Override
    public Provider<T> withScope(Key<?> match, Scope scope) {
        if (scope == Scopes.SINGLETON) {
            return this;
        } else {
            throw new UnsupportedOperationException("Instance providers cannot be scoped!");
        }
    }

    @Override
    public T get() {
        return instance;
    }

    @Override
    public String toString() {
        return "instance '" + instance + "'";
    }

}
