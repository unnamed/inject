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
package team.unnamed.inject.key;

import team.unnamed.inject.util.Validate;

import java.util.Objects;

/**
 * An extension for {@link Key} (using composition over inheritance)
 * that adds two boolean states representing the requirement of the
 * injection of this key and if this key will be assisted or not.
 */
public final class InjectedKey<T> {

    private final Key<T> key;
    private final boolean optional;
    private final boolean assisted;

    public InjectedKey(Key<T> key, boolean optional, boolean assisted) {
        this.key = Validate.notNull(key, "key");
        this.optional = optional;
        this.assisted = assisted;
    }

    public Key<T> getKey() {
        return key;
    }

    public boolean isOptional() {
        return optional;
    }

    public boolean isAssisted() {
        return assisted;
    }

    @Override
    public String toString() {
        return (optional ? "(optional) " : "(required) ")
                + (assisted ? "(assisted) " : "")
                + key.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InjectedKey<?> that = (InjectedKey<?>) o;
        return optional == that.optional
                && assisted == that.assisted
                && key.equals(that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(optional, assisted, key);
    }

}
