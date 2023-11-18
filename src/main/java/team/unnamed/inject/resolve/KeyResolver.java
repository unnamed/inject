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
package team.unnamed.inject.resolve;

import team.unnamed.inject.Qualifier;
import team.unnamed.inject.assisted.Assist;
import team.unnamed.inject.impl.Annotations;
import team.unnamed.inject.key.InjectedKey;
import team.unnamed.inject.key.Key;
import team.unnamed.inject.key.TypeReference;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public final class KeyResolver {

    KeyResolver() {
    }

    /**
     * Resolves the keys of the given parameters
     *
     * @param declaringType The declaring type of the field
     * @param parameters   The parameters to resolve
     * @return Resolves the key of the given parameter set and its annotations
     */
    public List<InjectedKey<?>> keysOf(
            TypeReference<?> declaringType,
            Parameter[] parameters
    ) {
        List<InjectedKey<?>> keys =
                new ArrayList<>(parameters.length);
        for (Parameter parameter : parameters) {
            Type type = parameter.getParameterizedType();
            Annotation[] annotations = parameter.getAnnotations();
            TypeReference<?> parameterType = declaringType.resolve(type);
            keys.add(keyOf(parameterType, annotations));
        }
        return keys;
    }

    public <T> InjectedKey<T> keyOf(
            TypeReference<T> type,
            Annotation[] annotations
    ) {
        boolean optional = false;
        boolean assisted = false;
        Class<? extends Annotation> qualifierType = null;
        Annotation qualifier = null;

        for (Annotation annotation : annotations) {
            Class<? extends Annotation> annotationType = annotation.annotationType();
            if (!optional) {
                String simpleName = annotationType.getSimpleName();
                // Please use "Nullable" instead of "nullable"
                if (simpleName.equalsIgnoreCase("Nullable")) {
                    optional = true;
                    continue;
                }
            }
            if (!assisted && annotationType == Assist.class) {
                assisted = true;
            }
            if (
                    qualifierType == null
                            && qualifier == null
                            && annotationType.isAnnotationPresent(Qualifier.class)
            ) {
                if (Annotations.containsOnlyDefaultValues(annotation)) {
                    qualifierType = annotationType;
                } else {
                    qualifier = annotation;
                }
            }
        }

        Key<T> key = Key.of(type, qualifierType, qualifier);
        return new InjectedKey<>(key, optional, assisted);
    }

}
