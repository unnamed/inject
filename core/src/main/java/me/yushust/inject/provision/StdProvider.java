package me.yushust.inject.provision;

import me.yushust.inject.key.Key;

import javax.inject.Provider;

public abstract class StdProvider<T> implements Provider<T> {

  private boolean injected; // TODO: Make this atomic

  public void setInjected(boolean injected) {
    this.injected = injected;
  }

  public boolean isInjected() {
    return injected;
  }

  @Override
  public T get() {
    return null;
  }

  public T get(Key<?> match) {
    return get();
  }

}
