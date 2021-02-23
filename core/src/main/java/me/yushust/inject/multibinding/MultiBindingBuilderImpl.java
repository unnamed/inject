package me.yushust.inject.multibinding;

import me.yushust.inject.Binder;
import me.yushust.inject.impl.*;
import me.yushust.inject.key.Key;
import me.yushust.inject.key.TypeReference;

import java.util.*;

public class MultiBindingBuilderImpl<T> implements
    Binder.MultiBindingBuilder<T>,
    KeyBuilder<Binder.MultiBindingBuilder<T>, T> {

  private Key<T> key;
  private final BinderImpl binder;

  public MultiBindingBuilderImpl(BinderImpl binder, TypeReference<T> key) {
    this.key = Key.of(key);
    this.binder = binder;
  }

  /** Starts building a binding using the given collection creator */
  @Override
  public Binder.CollectionMultiBindingBuilder<T> asCollection(Class<?> baseType, CollectionCreator collectionCreator) {
    Key<List<T>> listKey = Key.of(TypeReference.of(baseType, key.getType().getType()));
    return new CollectionMultiBindingBuilderImpl<>(binder, listKey, key, collectionCreator);
  }

  @Override
  public <K> Binder.MapMultiBindingBuilder<K, T> asMap(TypeReference<K> keyReference, MapCreator mapCreator) {
    Key<Map<K, T>> mapKey = Key.of(TypeReference.mapTypeOf(keyReference, key.getType()));
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
