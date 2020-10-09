package me.yushust.inject;

import me.yushust.inject.error.ErrorAttachable;
import me.yushust.inject.key.TypeReference;
import me.yushust.inject.scope.Scope;

import javax.inject.Provider;
import java.lang.annotation.Annotation;

// TODO: Add the EDSL specification
public interface Binder extends ErrorAttachable {

  <T> QualifiedBindingBuilder<T> bind(Class<T> keyType);

  <T> QualifiedBindingBuilder<T> bind(TypeReference<T> keyType);

  <T> MultiBindingBuilder<T> multibind(Class<T> keyType);

  <T> MultiBindingBuilder<T> multibind(TypeReference<T> keyType);

  void install(Module... modules);

  void install(Iterable<? extends Module> modules);

  /**
   * Represents a binding builder that can be
   * scoped. This interface marks the end of
   * the configuration of a binding
   */
  interface Scoped {

    /** Scopes the binding being built */
    void in(Scope scope);

    /** Alias method for ins(Scopes.SINGLETON) */
    void singleton();

  }

  /**
   * Represents a binding builder that can be
   * qualified, for example with an annotation,
   * an annotation type, etc.
   * @param <R> The return type for all the
   *           qualify methods
   */
  interface Qualified<R> {

    /** Qualifies the key with the specified annotation type */
    R markedWith(Class<? extends Annotation> qualifierType);

    /** Qualifies the key with the specific annotation instance */
    R qualified(Annotation annotation);

    /** Qualifies the key with the specific name */
    R named(String name);

  }

  /**
   * Represents a binding builder that can be
   * linked to another key (or the same key)
   * @param <R> The return type for the
   *           link creation methods
   * @param <T> The key being bound
   */
  interface Linked<R, T> {

    /** Links the key to a class */
    R to(Class<? extends T> targetType);

    /** Links the key to a (possible) generic type */
    R to(TypeReference<? extends T> targetType);

    /** Links the key to a specific provider */
    R toProvider(Provider<? extends T> provider);

    /** Links the key to a specific provider type */
    <P extends Provider<? extends T>> R toProvider(Class<P> providerClass);

    /** Links the key to a specific provider (possible) generic type */
    <P extends Provider<? extends T>> R toProvider(TypeReference<P> providerClass);

  }

  /**
   * Represents a binding builder that can be qualified,
   * linked and scoped. This is the principal binding
   * builder.
   * @param <T> The key being bound
   */
  interface QualifiedBindingBuilder<T> extends Qualified<QualifiedBindingBuilder<T>>, Linked<Scoped, T>, Scoped {

    /** Binds the key to a specific instance */
    void toInstance(T instance);

  }

  /**
   * Represents a binding builder for collections,
   * it can be qualified.
   * @param <T> The element key being bound
   */
  interface MultiBindingBuilder<T> extends Qualified<MultiBindingBuilder<T>> {

    /** Starts linking and scoping the element type as a Set */
    CollectionMultiBindingBuilder<T> asSet();

    /** Starts linking and scoping the element type as a List */
    CollectionMultiBindingBuilder<T> asList();

    /** Starts linking and scoping the element type as a Map with the specified key type */
    <K> MapMultiBindingBuilder<K, T> asMap(Class<K> keyClass);

    /** Starts linking and scoping the element type as a Map with the specified key type */
    <K> MapMultiBindingBuilder<K, T> asMap(TypeReference<K> keyReference);

  }

  /**
   * Represents a binding builder for collections,
   * it can be linked and scoped, it's qualified
   * using {@link MultiBindingBuilder}
   * @param <T> The collection element type
   */
  interface CollectionMultiBindingBuilder<T> extends Linked<CollectionMultiBindingBuilder<T>, T>, Scoped {

    /** Adds an instance of the specific element type to the collection */
    CollectionMultiBindingBuilder<T> toInstance(T instance);

  }

  /**
   * Represents a binding builder for maps,
   * binds using a key and a value. It can be
   * scoped.
   * @param <K> The map key type
   * @param <V> The map value type
   */
  interface MapMultiBindingBuilder<K, V> extends Scoped {

    /** Starts linking a key to a value */
    KeyBinder<K, V> bind(K key);

  }

  /**
   * Represents a map key that's being bound
   * to a value. It can be linked to a provider
   * @param <K> The map key type
   * @param <V> The map value type
   */
  interface KeyBinder<K, V> extends Linked<MapMultiBindingBuilder<K, V>, V> {

    /** Adds an instance of the specific value type to the map */
    MapMultiBindingBuilder<K, V> toInstance(V instance);

  }

}
