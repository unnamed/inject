package me.yushust.inject.provision.ioc;

import me.yushust.inject.assisted.provision.ToFactoryProvider;
import me.yushust.inject.internal.BinderImpl;
import me.yushust.inject.key.Key;

/**
 * Adds the control of the binding to the provider,
 * used by standard providers and it can be used for extensions.
 *
 * <p>The control is passed from the injector to the
 * provider. If a provider doesn't implement this interface,
 * the injector handles it.</p>
 */
public interface BindListener {

  /**
   * Listener method called when the provider
   * is bound. Commonly this is called immediately
   * after the construction.
   *
   * @param binder The binder used to bind this provider
   * @param key    The bound key
   * @return False if the binding must be removed (handled
   * by the binder) this is used for special providers like
   * the {@link ToFactoryProvider} that converts its binding
   * to another binding. (Removes its binding and adds another one)
   */
  boolean onBind(BinderImpl binder, Key<?> key);

}
