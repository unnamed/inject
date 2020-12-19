package me.yushust.inject.provision.ioc;

import me.yushust.inject.key.Key;

import javax.inject.Provider;

public interface MatchListener<T> extends Provider<T> {

  T get(Key<?> match);

}
