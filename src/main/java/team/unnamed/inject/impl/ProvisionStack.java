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
package team.unnamed.inject.impl;

import team.unnamed.inject.error.ErrorAttachableImpl;
import team.unnamed.inject.key.Key;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

public class ProvisionStack extends ErrorAttachableImpl {

    // Used to invoke an O(1) "get" method
    private final Map<Key<?>, Object> values =
            new HashMap<>();
    // The real provision Stack, contains a relation of
    private final LinkedList<KeyInstanceEntry<?>> stack =
            new LinkedList<>();

    public boolean has(Key<?> key) {
        return values.containsKey(key);
    }

    public <T> T get(Key<T> key) {
        // the cast is safe, the
        // map is modified only with the
        // generic method ProvisionStack#add(...)
        @SuppressWarnings("unchecked")
        T value = (T) values.get(key);
        return value;
    }

    public void pop() {
        Map.Entry<Key<?>, Object> entry = stack.removeFirst();
        if (entry != null) {
            values.remove(entry.getKey());
        }
    }

    public <T> void push(Key<T> key, T value) {
        values.put(key, value);
        stack.addFirst(new KeyInstanceEntry<>(key, value));
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append('(');
        builder.append(errorCount());
        builder.append(" errors");
        builder.append(") ");
        Iterator<KeyInstanceEntry<?>> entries = stack.iterator();
        while (entries.hasNext()) {
            KeyInstanceEntry<?> entry = entries.next();
            builder.append(entry.getKey());
            if (entries.hasNext()) {
                builder.append(" -> ");
            }
        }
        return builder.toString();
    }

    private static class KeyInstanceEntry<T> implements Map.Entry<Key<?>, Object> {

        private final Key<T> key;
        private final T value;

        public KeyInstanceEntry(Key<T> key, T value) {
            this.key = key;
            this.value = value;
        }

        public Key<?> getKey() {
            return key;
        }

        public Object getValue() {
            return value;
        }

        public T setValue(Object value) {
            // it's not really handled like
            // an entry, it's used like a javafx.util.Pair
            throw new UnsupportedOperationException("This entry is immutable!");
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            KeyInstanceEntry<?> that = (KeyInstanceEntry<?>) o;
            return key.equals(that.key) &&
                    value.equals(that.value);
        }

        @Override
        public int hashCode() {
            int result = 1;
            result = 31 * result + key.hashCode();
            result = 31 * result + value.hashCode();
            return result;
        }

    }

}
