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
