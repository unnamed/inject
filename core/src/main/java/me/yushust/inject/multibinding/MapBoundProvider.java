package me.yushust.inject.multibinding;

import me.yushust.inject.internal.InternalInjector;
import me.yushust.inject.internal.ProvisionStack;
import me.yushust.inject.key.Key;
import me.yushust.inject.key.TypeReference;
import me.yushust.inject.provision.DelegatingStdProvider;
import me.yushust.inject.provision.StdProvider;
import me.yushust.inject.provision.ioc.InjectionListener;
import me.yushust.inject.provision.ioc.ScopeListener;
import me.yushust.inject.scope.Scope;
import me.yushust.inject.util.Validate;

import javax.inject.Provider;
import java.util.Collections;
import java.util.Map;

class MapBoundProvider<K, V>
    extends StdProvider<Map<K, V>>
    implements InjectionListener, ScopeListener<Map<K, V>> {

  private final Map<K, Provider<? extends V>> delegates;
  private final MapCreator mapCreator;

  MapBoundProvider(MapCreator mapCreator) {
    this.delegates = mapCreator.create();
    this.mapCreator = mapCreator;
  }

  @Override
  public void onInject(ProvisionStack stack, InternalInjector injector) {
    delegates.forEach((key, valueProvider) -> {
      if (valueProvider instanceof InjectionListener) {
        ((InjectionListener) valueProvider).onInject(stack, injector);
      } else {
        injector.injectMembers(
            stack,
            Key.of(TypeReference.of(valueProvider.getClass())),
            valueProvider
        );
      }
    });
    setInjected(true);
  }

  @Override
  public Provider<Map<K, V>> withScope(Scope scope) {
    Validate.notNull(scope, "scope");
    return new DelegatingStdProvider<>(
        isInjected(),
        scope.scope(this)
    );
  }

  @Override
  public Map<K, V> get() {
    Map<K, V> map = mapCreator.create();
    delegates.forEach((key, valueProvider) ->
        map.put(key, valueProvider.get())
    );
    return map;
  }

  public Map<K, Provider<? extends V>> getProviders() {
    return Collections.unmodifiableMap(delegates);
  }

  Map<K, Provider<? extends V>> getModifiableProviderMap() {
    return delegates;
  }

  @Override
  public String toString() {
    return "MapMultiBound(" + delegates + ")";
  }
}
