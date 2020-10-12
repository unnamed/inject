package me.yushust.inject.internal;

import me.yushust.inject.Binder;
import me.yushust.inject.key.Key;
import me.yushust.inject.key.TypeReference;
import me.yushust.inject.resolve.QualifierFactory;
import me.yushust.inject.scope.Scope;
import me.yushust.inject.util.Validate;

import javax.inject.Provider;
import java.util.*;

class MultiBindingBuilderImpl<T> implements
    Binder.MultiBindingBuilder<T>,
    KeyBuilder<Binder.MultiBindingBuilder<T>, T> {

  private final QualifierFactory qualifierFactory;
  private Key<T> key;
  private final BinderImpl binder;

  MultiBindingBuilderImpl(QualifierFactory qualifierFactory, BinderImpl binder, TypeReference<T> key) {
    this.qualifierFactory = qualifierFactory;
    this.key = Key.of(key);
    this.binder = binder;
  }

  @Override
  public Binder.CollectionMultiBindingBuilder<T> asSet() {
    Key<Set<T>> setKey = Key.of(TypeReference.of(Set.class, key.getType().getType()));
    return new CollectionMultiBindingBuilderImpl<>(setKey, key, HashSet::new);
  }

  @Override
  public Binder.CollectionMultiBindingBuilder<T> asList() {
    Key<List<T>> listKey = Key.of(TypeReference.of(List.class, key.getType().getType()));
    return new CollectionMultiBindingBuilderImpl<>(listKey, key, ArrayList::new);
  }

  @Override
  public <K> Binder.MapMultiBindingBuilder<K, T> asMap(TypeReference<K> keyReference) {
    Key<Map<K, T>> mapKey = Key.of(TypeReference.of(Map.class, keyReference.getType(), key.getType().getType()));
    return new MapMultiBindingBuilderImpl<>(HashMap::new, mapKey, key);
  }

  @Override
  public QualifierFactory factory() {
    return qualifierFactory;
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

  /**
   * Represents a Collection Binding Builder, with this
   * builder you add the element providers
   * @param <E> The type of the elements
   */
  private class CollectionMultiBindingBuilderImpl<E> implements
      Binder.CollectionMultiBindingBuilder<E>,
      LinkedBuilder<Binder.CollectionMultiBindingBuilder<E>, E> {

    private final Key<? extends Collection<E>> collectionKey;
    private final Key<E> elementKey;

    private final CollectionCreator collectionCreator;

    protected CollectionMultiBindingBuilderImpl(Key<? extends Collection<E>> collectionKey,
                                                Key<E> elementKey, CollectionCreator collectionCreator) {
      this.collectionKey = collectionKey;
      this.elementKey = elementKey;
      this.collectionCreator = collectionCreator;
    }

    @Override
    public void in(Scope scope) {
      Validate.notNull(scope, "scope");
      InjectedProvider<? extends Collection<E>> provider = binder.getProvider(collectionKey);
      if (provider != null) {
        binder.$unsafeBind(collectionKey, provider.withScope(scope));
      }
    }

    @Override
    public Key<E> key() {
      return elementKey;
    }

    @Override
    public Binder.CollectionMultiBindingBuilder<E> toProvider(Provider<? extends E> provider) {

      InjectedProvider<? extends Collection<E>> collectionProvider = binder.getProvider(collectionKey);

      if (collectionProvider == null) {
        collectionProvider = new CollectionProvider<>(collectionCreator);
        binder.$unsafeBind(collectionKey, collectionProvider);
      }

      Provider<? extends Collection<E>> delegate = collectionProvider.getDelegate();
      if (!(delegate instanceof CollectionProvider)) {
        throw new IllegalStateException("The key '" + collectionKey
            + "' is already bound and it isn't a multibinding!");
      }
      @SuppressWarnings("unchecked")
      CollectionProvider<E> collectionDelegate =
          (CollectionProvider<E>) delegate;
      collectionDelegate.delegates.add(provider);
      return this;
    }

    @Override
    public Binder.CollectionMultiBindingBuilder<E> toInstance(E instance) {
      return toProvider(Providers.instanceProvider(elementKey, instance));
    }

  }

  /**
   * Represents a Collection provider that delegates all the
   * injection to the element providers.
   * @param <E> The element type
   */
  static class CollectionProvider<E> extends InjectedProvider<Collection<E>> {

    private final Collection<Provider<? extends E>> delegates;
    private final CollectionCreator collectionCreator;

    CollectionProvider(CollectionCreator collectionCreator) {
      super(false, () -> null);
      this.collectionCreator = collectionCreator;
      this.delegates = collectionCreator.create();
    }

    /** Injects members of all element providers */
    @Override
    void inject(ProvisionStack stack, InternalInjector injector) {
      for (Provider<? extends E> provider : delegates) {
        if (provider instanceof InjectedProvider) {
          ((InjectedProvider<?>) provider).inject(stack, injector);
        } else {
          injector.injectMembers(
              stack,
              Key.of(TypeReference.of(provider.getClass())),
              provider
          );
        }
      }
      setInjected(true);
    }

    @Override
    InjectedProvider<Collection<E>> withScope(Scope scope) {
      return new InjectedProvider<>(
          isInjected(),
          scope.scope(this)
      );
    }

    @Override
    Provider<Collection<E>> getDelegate() {
      return this;
    }

    @Override
    public Collection<E> get() {
      Collection<E> collection = collectionCreator.create();
      for (Provider<? extends E> delegate : delegates) {
        collection.add(delegate.get());
      }
      return collection;
    }

    @Override
    public String toString() {
      return "CollectionMultiBound(" + delegates + ")";
    }
  }

  interface CollectionCreator {
    <E> Collection<E> create();
  }

  interface MapCreator {
    <K, V> Map<K, V> create();
  }

  private class MapMultiBindingBuilderImpl<K, V> implements Binder.MapMultiBindingBuilder<K, V> {

    private final MapCreator mapCreator;
    private final Key<Map<K, V>> mapKey;
    private final Key<V> valueKey;

    private MapMultiBindingBuilderImpl(MapCreator mapCreator, Key<Map<K, V>> mapKey, Key<V> valueKey) {
      this.mapCreator = mapCreator;
      this.mapKey = mapKey;
      this.valueKey = valueKey;
    }

    @Override
    public void in(Scope scope) {
      Validate.notNull(scope, "scope");
      InjectedProvider<? extends Map<K, V>> provider =
          binder.getProvider(mapKey);
      if (provider != null) {
        binder.$unsafeBind(mapKey, provider.withScope(scope));
      }
    }

    @Override
    public Binder.KeyBinder<K, V> bind(K key) {
      return new KeyBinderImpl<>(this, key);
    }
  }

  private class KeyBinderImpl<K, V> implements
      Binder.KeyBinder<K, V>,
      LinkedBuilder<Binder.MapMultiBindingBuilder<K, V>, V> {

    private final MapMultiBindingBuilderImpl<K, V> bindingBuilder;
    private final K key;

    private KeyBinderImpl(MapMultiBindingBuilderImpl<K, V> bindingBuilder, K key) {
      this.key = key;
      this.bindingBuilder = bindingBuilder;
    }

    @Override
    public Key<V> key() {
      return bindingBuilder.valueKey;
    }

    @Override
    public Binder.MapMultiBindingBuilder<K, V> toProvider(Provider<? extends V> provider) {
      InjectedProvider<? extends Map<K, V>> mapProvider = binder.getProvider(bindingBuilder.mapKey);

      if (mapProvider == null) {
        mapProvider = new MapProvider<>(bindingBuilder.mapCreator);
        binder.$unsafeBind(bindingBuilder.mapKey, mapProvider);
      }

      Provider<? extends Map<K, V>> delegate = mapProvider.getDelegate();
      if (!(delegate instanceof MapProvider)) {
        throw new IllegalStateException("The key '" + bindingBuilder.mapKey
            + "' is already bound and it isn't a multibinding!");
      }
      @SuppressWarnings("unchecked")
      MapProvider<K, V> collectionDelegate =
          (MapProvider<K, V>) delegate;
      collectionDelegate.delegates.put(key, provider);
      return bindingBuilder;
    }

    @Override
    public Binder.MapMultiBindingBuilder<K, V> toInstance(V instance) {
      return toProvider(Providers.instanceProvider(key(), instance));
    }
  }

  private static class MapProvider<K, V> extends InjectedProvider<Map<K, V>> {

    private final Map<K, Provider<? extends V>> delegates;
    private final MapCreator mapCreator;

    MapProvider(MapCreator mapCreator) {
      super(false, () -> null);
      this.delegates = mapCreator.create();
      this.mapCreator = mapCreator;
    }

    @Override
    void inject(ProvisionStack stack, InternalInjector injector) {
      delegates.forEach((key, valueProvider) -> {
        if (valueProvider instanceof InjectedProvider) {
          ((InjectedProvider<?>) valueProvider).inject(stack, injector);
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
    InjectedProvider<Map<K, V>> withScope(Scope scope) {
      return new InjectedProvider<>(
          isInjected(),
          scope.scope(this)
      );
    }

    @Override
    Provider<Map<K, V>> getDelegate() {
      return this;
    }

    @Override
    public Map<K, V> get() {
      Map<K, V> map = mapCreator.create();
      delegates.forEach((key, valueProvider) ->
        map.put(key, valueProvider.get())
      );
      return map;
    }

    @Override
    public String toString() {
      return "MapMultiBound(" + delegates + ")";
    }
  }

}
