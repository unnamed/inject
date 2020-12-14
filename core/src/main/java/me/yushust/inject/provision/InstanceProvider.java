package me.yushust.inject.provision;

import me.yushust.inject.internal.InjectedProvider;
import me.yushust.inject.internal.InternalInjector;
import me.yushust.inject.internal.ProvisionStack;
import me.yushust.inject.key.Key;
import me.yushust.inject.key.TypeReference;
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
public class InstanceProvider<T> extends InjectedProvider<T> {

  private final Key<T> key;
  private final T instance;

  public InstanceProvider(Key<T> key, T instance) {
    super(false, () -> null);
    this.key = key;
    this.instance = instance;
  }

  public InstanceProvider(T instance) {
    this(
        Key.of(TypeReference.of(instance.getClass())),
        instance
    );
  }

  @Override
  public void inject(ProvisionStack stack, InternalInjector injector) {
    // TODO: This shit won't work til 2021. injector.injectMembers(stack, key.withNoQualifiers(), instance);
  }

  @Override
  public InjectedProvider<T> withScope(Scope scope) {
    if (scope == Scopes.SINGLETON) {
      return this;
    }
    return new InjectedProvider<>(
        isInjected(),
        scope.scope(this)
    );
  }

  @Override
  public Provider<T> getDelegate() {
    return this;
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
