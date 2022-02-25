package team.unnamed.inject.multibinding;

import team.unnamed.inject.Binder;
import team.unnamed.inject.impl.BinderImpl;
import team.unnamed.inject.impl.LinkedBuilder;
import team.unnamed.inject.key.Key;
import team.unnamed.inject.provision.Providers;
import team.unnamed.inject.provision.StdProvider;
import team.unnamed.inject.scope.Scope;
import team.unnamed.inject.util.Validate;

import javax.inject.Provider;
import java.util.Map;

class MapMultiBindingBuilderImpl<K, V> implements Binder.MapMultiBindingBuilder<K, V> {

    private final BinderImpl binder;
    private final MapCreator mapCreator;
    private final Key<Map<K, V>> mapKey;
    private final Key<V> valueKey;

    MapMultiBindingBuilderImpl(BinderImpl binder, MapCreator mapCreator, Key<Map<K, V>> mapKey, Key<V> valueKey) {
        this.binder = binder;
        this.mapCreator = mapCreator;
        this.mapKey = mapKey;
        this.valueKey = valueKey;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void in(Scope scope) {
        Validate.notNull(scope, "scope");
        Provider<? extends Map<K, V>> provider = Providers.unwrap(binder.getProvider(mapKey));
        if (provider != null) {
            if (provider instanceof StdProvider) {
                provider = ((StdProvider<? extends Map<K, V>>) provider)
                        .withScope(mapKey, scope);
            } else {
                provider = scope.scope(provider);
            }
            binder.$unsafeBind(mapKey, provider);
        }
    }

    @Override
    public Binder.KeyBinder<K, V> bind(K key) {
        return new KeyBinderImpl(key);
    }

    class KeyBinderImpl implements
            Binder.KeyBinder<K, V>,
            LinkedBuilder<Binder.MapMultiBindingBuilder<K, V>, V> {

        private final K key;

        private KeyBinderImpl(K key) {
            this.key = key;
        }

        @Override
        public Key<V> key() {
            return valueKey;
        }

        @Override
        public Binder.MapMultiBindingBuilder<K, V> toProvider(Provider<? extends V> provider) {
            Validate.notNull(provider, "provider");
            StdProvider<? extends Map<K, V>> mapProvider = binder.getProvider(mapKey);

            if (mapProvider == null) {
                mapProvider = new MapBoundProvider<>(mapCreator);
                binder.$unsafeBind(mapKey, mapProvider);
            }

            Provider<? extends Map<K, V>> delegate = Providers.unwrap(mapProvider);
            if (!(delegate instanceof MapBoundProvider)) {
                throw new IllegalStateException("The key '" + mapKey
                        + "' is already bound and it isn't a multibinding!");
            }
            @SuppressWarnings("unchecked")
            MapBoundProvider<K, V> collectionDelegate =
                    (MapBoundProvider<K, V>) delegate;
            collectionDelegate.getModifiableProviderMap().put(key, provider);
            return MapMultiBindingBuilderImpl.this;
        }

        @Override
        public Binder.MapMultiBindingBuilder<K, V> toInstance(V instance) {
            return toProvider(Providers.instanceProvider(key(), instance));
        }

    }

}
