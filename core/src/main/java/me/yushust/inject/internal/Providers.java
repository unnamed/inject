package me.yushust.inject.internal;

import me.yushust.inject.key.Key;
import me.yushust.inject.key.TypeReference;
import me.yushust.inject.scope.Scope;
import me.yushust.inject.scope.Scopes;
import me.yushust.inject.util.Validate;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * Collection of static factory methods to create providers
 */
public final class Providers {

  private Providers() {
    throw new UnsupportedOperationException("This class couldn't be instantiated!");
  }

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
   *
   * The bound instances are also injected
   * with {@link me.yushust.inject.Injector#injectMembers}
   */
  private static class InstanceProvider<T> extends InjectedProvider<T> {

    private final Key<T> key;
    private final T instance;

    private InstanceProvider(Key<T> key, T instance) {
      super(false, () -> null);
      this.key = key;
      this.instance = instance;
    }

    @Override
    void inject(ProvisionStack stack, InternalInjector injector) {
      injector.injectMembers(stack, key.withNoQualifiers(), instance);
    }

    @Override
    InjectedProvider<T> withScope(Scope scope) {
      if (scope == Scopes.SINGLETON) {
        return this;
      }
      return new InjectedProvider<>(
          isInjected(),
          scope.scope(this)
      );
    }

    @Override
    Provider<T> getDelegate() {
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

  public static <T> Provider<? extends T> instanceProvider(Key<T> key, T instance) {
    Validate.notNull(key, "key");
    Validate.notNull(instance, "instance");
    return new InstanceProvider<>(key, instance);
  }

  /**
   * Represents a provider that gets instantiated the first
   * time that gets called, then the instance is saved.
   * The get() method functionality is delegated to the
   * delegate instance.
   *
   * <p>For example</p>
   * <sub>
   *   public interface Foo {
   *     // ...
   *   }
   *   public class FooProvider implements Provider&#60;Foo&#62; {
   *
   *     &#64;Inject private Baz baz;
   *
   *     &#64;Override
   *     public Foo get() {
   *       // create Foo using some baz property
   *     }
   *   }
   * </sub>
   *
   * Bindings like
   * {@code bind(Foo.class).toProvider(FooProvider.class)} and
   * {@code bind(Foo.class).toProvider(new FooProvider())} works
   *
   * The difference is that the binding to the provider class
   * creates the provider instance using {@link me.yushust.inject.Injector#getInstance},
   * so the constructor can be injected
   */
  private static class ProviderTypeProvider<T> implements Provider<T> {

    private final TypeReference<? extends Provider<? extends T>> providerClass;
    private volatile Provider<? extends T> provider;

    private ProviderTypeProvider(TypeReference<? extends Provider<? extends T>> providerClass) {
      this.providerClass = providerClass;
    }

    @Inject
    public void inject(InternalInjector injector) {
      provider = injector.getInstance(providerClass);
    }

    @Override
    public T get() {
      if (provider == null) {
        return null;
      }
      return provider.get();
    }

    @Override
    public String toString() {
      return "provider '" + providerClass + "'";
    }
  }

  public static <T> Provider<? extends T> providerTypeProvider(TypeReference<? extends Provider<? extends T>> providerClass) {
    Validate.notNull(providerClass);
    return new ProviderTypeProvider<>(providerClass);
  }

  /**
   * Represents a link of a key to another key (can be the same).
   * The provider gets an instance of the target type instead
   * of getting an instance of the original key.
   *
   * <p>For this example</p>
   * <pre>
   * public interface Foo {
   *   // ...
   * }
   *
   * public class Bar implements Foo {
   *   // ...
   * }
   * </pre>
   * <p>The link will be</p>
   * {@code bind(Foo.class).to(Bar.class);}
   */
  private static class LinkedProvider<T> implements Provider<T> {

    private final Key<T> key;
    private final Key<? extends T> target;
    @Inject private InternalInjector injector;

    private LinkedProvider(Key<T> key, Key<? extends T> target) {
      this.key = key;
      this.target = target;
    }

    @Override
    public T get() {
      // the injector should not use the explicit
      // bindings if the key is bound to the same
      // key. Else, it will call this get() method
      // again, and again, ending in a StackOverflowError
      return injector.getInstance(injector.stackForThisThread(), target, !key.equals(target));
    }

    @Override
    public String toString() {
      if (key.equals(target)) {
        return "same key";
      } else {
        return "linked key '" + target + "'";
      }
    }
  }

  public static <T> Provider<? extends T> link(Key<T> key, Key<? extends T> target) {
    Validate.notNull(key, "key");
    Validate.notNull(target, "target");
    return new LinkedProvider<>(key, target);
  }

}
