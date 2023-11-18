/*
 * This file is part of inject, licensed under the MIT license
 *
 * Copyright (c) 2021-2023 Unnamed Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package team.unnamed.inject;

import team.unnamed.inject.assisted.ValueFactory;
import team.unnamed.inject.error.ErrorAttachable;
import team.unnamed.inject.key.Key;
import team.unnamed.inject.key.TypeReference;
import team.unnamed.inject.multibinding.CollectionCreator;
import team.unnamed.inject.multibinding.MapCreator;
import team.unnamed.inject.provision.std.generic.GenericProvider;
import team.unnamed.inject.scope.Scope;
import team.unnamed.inject.scope.Scopes;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// TODO: Add the EDSL specification
public interface Binder extends ErrorAttachable {

    /**
     * @deprecated Unsafe operation, the method is here
     * to avoid the usage of raw-types in some cases.
     * If you misuse it, everything will end up bugged.
     *
     * <p>The method directly puts the key with the
     * specified provider to the map, it's not checked
     * so it's unsafe.</p>
     */
    @Deprecated
    void $unsafeBind(Key<?> key, Provider<?> provider);

    default <T> QualifiedBindingBuilder<T> bind(Class<T> keyType) {
        return bind(TypeReference.of(keyType));
    }

    <T> QualifiedBindingBuilder<T> bind(TypeReference<T> keyType);

    default <T> MultiBindingBuilder<T> multibind(Class<T> keyType) {
        return multibind(TypeReference.of(keyType));
    }

    <T> MultiBindingBuilder<T> multibind(TypeReference<T> keyType);

    default void install(Module... modules) {
        install(Arrays.asList(modules));
    }

    void install(Iterable<? extends Module> modules);

    /**
     * Represents a binding builder that can be
     * scoped. This interface marks the end of
     * the configuration of a binding
     */
    interface Scoped {

        /**
         * Scopes the binding being built
         */
        void in(Scope scope);

        /**
         * Alias method for in(Scopes.SINGLETON)
         */
        default void singleton() {
            in(Scopes.SINGLETON);
        }

    }

    /**
     * Represents a binding builder that can be
     * qualified, for example with an annotation,
     * an annotation type, etc.
     *
     * @param <R> The return type for all the
     *            qualify methods
     */
    interface Qualified<R> {

        /**
         * Qualifies the key with the specified annotation type
         */
        R markedWith(Class<? extends Annotation> qualifierType);

        /**
         * Qualifies the key with the specific annotation instance
         */
        R qualified(Annotation annotation);

        /**
         * Qualifies the key with the specific name
         */
        R named(String name);

    }

    /**
     * Represents a binding builder that can be
     * linked to another key (or the same key)
     *
     * @param <R> The return type for the
     *            link creation methods
     * @param <T> The key being bound
     */
    interface Linked<R, T> {

        /**
         * Links the key to a class
         */
        default R to(Class<? extends T> targetType) {
            return to(TypeReference.of(targetType));
        }

        /**
         * Links the key to a (possible) generic type
         */
        R to(TypeReference<? extends T> targetType);

        /**
         * Links the key to a specific provider
         */
        R toProvider(Provider<? extends T> provider);

        /**
         * Links the key to a generic provider
         */
        R toGenericProvider(GenericProvider<? extends T> provider);

        /**
         * Links the key to an assisted instance factory
         */
        default void toFactory(Class<? extends ValueFactory> factory) {
            toFactory(TypeReference.of(factory));
        }

        /**
         * Links the key to an assisted instance factory
         */
        void toFactory(TypeReference<? extends ValueFactory> factory);

        /**
         * Links the key to a specific provider type
         */
        default <P extends Provider<? extends T>> R toProvider(Class<P> providerClass) {
            return toProvider(TypeReference.of(providerClass));
        }

        /**
         * Links the key to a specific provider (possible) generic type
         */
        <P extends Provider<? extends T>> R toProvider(TypeReference<P> providerClass);

    }

    /**
     * Represents a binding builder that can be qualified,
     * linked and scoped. This is the principal binding
     * builder.
     *
     * @param <T> The key being bound
     */
    interface QualifiedBindingBuilder<T> extends Qualified<QualifiedBindingBuilder<T>>, Linked<Scoped, T>, Scoped {

        /**
         * Binds the key to a specific instance
         */
        void toInstance(T instance);

    }

    /**
     * Represents a binding builder for collections,
     * it can be qualified.
     *
     * @param <T> The element key being bound
     */
    interface MultiBindingBuilder<T> extends Qualified<MultiBindingBuilder<T>> {

        /**
         * Starts linking and scoping the element type as a Set
         */
        default CollectionMultiBindingBuilder<T> asSet() {
            return asCollection(Set.class, HashSet::new);
        }

        /**
         * Starts linking and scoping the element type as a List
         */
        default CollectionMultiBindingBuilder<T> asList() {
            return asCollection(List.class, ArrayList::new);
        }

        /**
         * Starts linking and scoping the element type using the collection creator returned instances
         */
        default CollectionMultiBindingBuilder<T> asCollection(CollectionCreator collectionCreator) {
            return asCollection(Collection.class, collectionCreator);
        }

        /**
         * Starts linking and scoping the element type using the collection creator returned instances
         */
        CollectionMultiBindingBuilder<T> asCollection(Class<?> baseType, CollectionCreator collectionCreator);

        /**
         * Starts linking and scoping the element type as a Map with the specified key type
         */
        default <K> MapMultiBindingBuilder<K, T> asMap(Class<K> keyClass) {
            return asMap(keyClass, HashMap::new);
        }

        /**
         * Starts linking and scoping the element type as a Map with the specified key type and map creator
         */
        default <K> MapMultiBindingBuilder<K, T> asMap(Class<K> keyClass, MapCreator mapCreator) {
            return asMap(TypeReference.of(keyClass), mapCreator);
        }

        /**
         * Starts linking and scoping the element type as a Map with the specified key type
         */
        default <K> MapMultiBindingBuilder<K, T> asMap(TypeReference<K> keyReference) {
            return asMap(keyReference, HashMap::new);
        }

        /**
         * Starts linking and scoping the element type as a Map with the specified key type and map creator
         */
        <K> MapMultiBindingBuilder<K, T> asMap(TypeReference<K> keyReference, MapCreator mapCreator);

    }

    /**
     * Represents a binding builder for collections,
     * it can be linked and scoped, it's qualified
     * using {@link MultiBindingBuilder}
     *
     * @param <T> The collection element type
     */
    interface CollectionMultiBindingBuilder<T> extends Linked<CollectionMultiBindingBuilder<T>, T>, Scoped {

        /**
         * Adds an instance of the specific element type to the collection
         */
        CollectionMultiBindingBuilder<T> toInstance(T instance);

    }

    /**
     * Represents a binding builder for maps,
     * binds using a key and a value. It can be
     * scoped.
     *
     * @param <K> The map key type
     * @param <V> The map value type
     */
    interface MapMultiBindingBuilder<K, V> extends Scoped {

        /**
         * Starts linking a key to a value
         */
        KeyBinder<K, V> bind(K key);

    }

    /**
     * Represents a map key that's being bound
     * to a value. It can be linked to a provider
     *
     * @param <K> The map key type
     * @param <V> The map value type
     */
    interface KeyBinder<K, V> extends Linked<MapMultiBindingBuilder<K, V>, V> {

        /**
         * Adds an instance of the specific value type to the map
         */
        MapMultiBindingBuilder<K, V> toInstance(V instance);

    }

}
