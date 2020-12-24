package me.yushust.inject.provision.std;

import me.yushust.inject.internal.InjectorImpl;
import me.yushust.inject.internal.ProvisionStack;
import me.yushust.inject.key.TypeReference;
import me.yushust.inject.provision.ioc.InjectionListener;
import me.yushust.inject.provision.StdProvider;

import javax.inject.Provider;

/**
 * Represents a provider that gets instantiated the first
 * time that gets called, then the instance is saved.
 * The get() method functionality is delegated to the
 * delegate instance.
 *
 * <p>For example</p>
 * <sub>
 * public interface Foo {
 * // ...
 * }
 * public class FooProvider implements Provider&#60;Foo&#62; {
 * <p>
 * &#64;Inject private Baz baz;
 * <p>
 * &#64;Override
 * public Foo get() {
 * // create Foo using some baz property
 * }
 * }
 * </sub>
 * <p>
 * Bindings like
 * {@code bind(Foo.class).toProvider(FooProvider.class)} and
 * {@code bind(Foo.class).toProvider(new FooProvider())} works
 * <p>
 * The difference is that the binding to the provider class
 * creates the provider instance using {@link me.yushust.inject.Injector#getInstance},
 * so the constructor can be injected
 */
public class ProviderTypeProvider<T>
    extends StdProvider<T>
    implements Provider<T>, InjectionListener {

  private final TypeReference<? extends Provider<? extends T>> providerClass;
  private volatile Provider<? extends T> provider;

  public ProviderTypeProvider(TypeReference<? extends Provider<? extends T>> providerClass) {
    this.providerClass = providerClass;
  }

  @Override
  public void onInject(ProvisionStack stack, InjectorImpl injector) {
    provider = injector.getInstance(providerClass);
  }

  @Override
  public T get() {
    return provider.get();
  }

  /** @return The targeted provider instance */
  public Provider<? extends T> getProvider() {
    return provider;
  }

  @Override
  public String toString() {
    return "ClassProvider(" + providerClass + ")";
  }

}
