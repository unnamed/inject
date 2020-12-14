package me.yushust.inject.provision;

import me.yushust.inject.internal.BinderImpl;
import me.yushust.inject.key.Key;

public interface BindListener {

  void onBind(BinderImpl binder, Key<?> key);

}
