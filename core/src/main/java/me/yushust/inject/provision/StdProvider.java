package me.yushust.inject.provision;

import javax.inject.Provider;

public abstract class StdProvider<T> implements Provider<T> {

  private boolean injected; // TODO: Make this atomic

  public void setInjected(boolean injected) {
    this.injected = injected;
  }

  public boolean isInjected() {
    return injected;
  }

}
