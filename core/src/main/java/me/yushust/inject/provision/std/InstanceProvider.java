package me.yushust.inject.provision.std;

import me.yushust.inject.key.Key;
import me.yushust.inject.provision.StdProvider;
import me.yushust.inject.scope.Scope;
import me.yushust.inject.scope.Scopes;

import javax.inject.Provider;

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
 * The bound instances are also injected
 * with {@link me.yushust.inject.Injector#injectMembers}
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
    }
    throw new UnsupportedOperationException("Instance providers cannot be scoped!");
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
