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

import team.unnamed.inject.Binder;
import team.unnamed.inject.key.Key;
import team.unnamed.inject.util.Validate;

import java.lang.annotation.Annotation;

/**
 * Removes the responsibility to the implementer class
 * of implement this methods. This interface behaves
 * like an abstract class (it's not an abstract class
 * because sometimes we need multiple "super-classes")
 */
public interface KeyBuilder<R, T> extends Binder.Qualified<R> {

    Key<T> key();

    void setKey(Key<T> key);

    @Override
    default R markedWith(Class<? extends Annotation> qualifierType) {
        Validate.notNull(qualifierType, "qualifierType");
        setKey(key().withQualifier(qualifierType));
        return getReturnValue();
    }

    @Override
    default R qualified(Annotation annotation) {
        Validate.notNull(annotation, "annotation");
        setKey(key().withQualifier(annotation));
        return getReturnValue();
    }

    @Override
    default R named(String name) {
        Validate.notNull(name, "name");
        return qualified(Annotations.createNamed(name));
    }

    R getReturnValue();

}
