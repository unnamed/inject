package me.yushust.inject.scope;

import javax.inject.Provider;

/**
 * Collection of built-in scopes
 */
public final class Scopes {

  public static final Scope SINGLETON = new SingletonScope();
  public static final Scope NONE = EmptyScope.INSTANCE;

  private Scopes() {
    throw new UnsupportedOperationException("This class couldn't be instantiated!");
  }

  private enum EmptyScope implements Scope {
    INSTANCE;

    public <T> Provider<T> scope(Provider<T> unscoped) {
      return unscoped;
    }
  }

}
