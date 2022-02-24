package me.yushust.inject.multibinding;

import me.yushust.inject.impl.InjectorImpl;
import me.yushust.inject.impl.ProvisionStack;
import me.yushust.inject.provision.Providers;
import me.yushust.inject.provision.StdProvider;

import javax.inject.Provider;
import java.util.Collections;
import java.util.Map;

class MapBoundProvider<K, V>
        extends StdProvider<Map<K, V>> {

    private final Map<K, Provider<? extends V>> delegates;
    private final MapCreator mapCreator;

    MapBoundProvider(MapCreator mapCreator) {
        this.delegates = mapCreator.create();
        this.mapCreator = mapCreator;
    }

    @Override
    public void inject(ProvisionStack stack, InjectorImpl injector) {
        delegates.forEach((key, valueProvider) -> Providers.inject(stack, injector, valueProvider));
        injected = true;
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
