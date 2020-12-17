package me.yushust.inject.provision.ioc;

import javax.inject.Provider;

public interface WrapperProvider<T> extends Provider<T> {

  Provider<T> getDelegate();

}
