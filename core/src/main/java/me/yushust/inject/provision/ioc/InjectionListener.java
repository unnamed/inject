package me.yushust.inject.provision.ioc;

import me.yushust.inject.Injector;
import me.yushust.inject.impl.InjectorImpl;
import me.yushust.inject.impl.ProvisionStack;

/**
 * Replaces the reflect-based injection of
 * providers by a simple method call (unique
 * method of this listener)
 *
 * <p>Used by standard providers and some other
 * providers to delegate the injection to the
 * wrapped provider (when it's a wrapper provider)
 * or injecting some provider properties without
 * checking everything</p>
 */
public interface InjectionListener {

  /**
   * Called instead of {@link Injector#injectMembers}(provider) when
   * implemented. (injectMembers is called when providers doesn't
   * implement this interface)
   *
   * @param stack    The thread injection stack (passed as argument
   *                 instead of getting it from the thread-local held
   *                 by the injector)
   * @param injector The injector used to inject this provider
   */
  void onInject(ProvisionStack stack, InjectorImpl injector);

}
