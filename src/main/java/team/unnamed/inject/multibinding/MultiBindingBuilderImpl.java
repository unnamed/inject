package team.unnamed.inject.multibinding;

import team.unnamed.inject.Binder;
import team.unnamed.inject.impl.BinderImpl;
import team.unnamed.inject.impl.KeyBuilder;
import team.unnamed.inject.key.Key;
import team.unnamed.inject.key.TypeReference;

import java.util.List;
import java.util.Map;

public class MultiBindingBuilderImpl<T> implements
        Binder.MultiBindingBuilder<T>,
        KeyBuilder<Binder.MultiBindingBuilder<T>, T> {

    private final BinderImpl binder;
    private Key<T> key;

    public MultiBindingBuilderImpl(BinderImpl binder, TypeReference<T> key) {
        this.key = Key.of(key);
        this.binder = binder;
    }

    /**
     * Starts building a binding using the given collection creator
     */
    @Override
    public Binder.CollectionMultiBindingBuilder<T> asCollection(Class<?> baseType, CollectionCreator collectionCreator) {
        Key<List<T>> listKey = key.withType(TypeReference.of(baseType, key.getType().getType()));
        return new CollectionMultiBindingBuilderImpl<>(binder, listKey, key, collectionCreator);
    }

    @Override
    public <K> Binder.MapMultiBindingBuilder<K, T> asMap(TypeReference<K> keyReference, MapCreator mapCreator) {
        Key<Map<K, T>> mapKey = key.withType(TypeReference.mapTypeOf(keyReference, key.getType()));
        return new MapMultiBindingBuilderImpl<>(binder, mapCreator, mapKey, key);
    }

    @Override
    public Key<T> key() {
        return key;
    }

    @Override
    public void setKey(Key<T> key) {
        this.key = key;
    }

    @Override
    public Binder.MultiBindingBuilder<T> getReturnValue() {
        return this;
    }

}
