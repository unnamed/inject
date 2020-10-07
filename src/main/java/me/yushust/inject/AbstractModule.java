package me.yushust.inject;

import me.yushust.inject.key.TypeReference;
import me.yushust.inject.util.Validate;

public abstract class AbstractModule implements Module {

  private Binder binder;

  @Override
  public final void configure(Binder binder) {
    Validate.state(this.binder == null, "The binder is already being configured by this module!");
    this.binder = binder;
    configure();
    this.binder = null;
  }

  /** The binder field isn't used directly because we need to
   * check if the binder is present, throwing the correct exception
   * instead of a simple and not descriptive null pointer exception */
  protected final Binder binder() {
    Validate.state(binder != null, "The binder isn't specified yet!");
    return binder;
  }

  protected final <T> Binder.Qualified<T> bind(Class<T> keyType) {
    return binder().bind(keyType);
  }

  protected final <T> Binder.Qualified<T> bind(TypeReference<T> keyType) {
    return binder().bind(keyType);
  }

  protected final void install(Module... modules) {
    binder().install(modules);
  }

  protected final void install(Iterable<? extends Module> modules) {
    binder().install(modules);
  }

  protected abstract void configure();

}
