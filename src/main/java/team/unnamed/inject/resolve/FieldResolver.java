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

import team.unnamed.inject.Inject;
import team.unnamed.inject.InjectAll;
import team.unnamed.inject.InjectIgnore;
import team.unnamed.inject.key.InjectedKey;
import team.unnamed.inject.key.TypeReference;
import team.unnamed.inject.resolve.solution.InjectableField;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public final class FieldResolver {

    FieldResolver() {
    }

    /**
     * Cached alternative method for {@link FieldResolver#resolve}
     *
     * @param type The type to resolve
     * @return The injectable fields
     */
    public List<InjectableField> get(TypeReference<?> type) {
        Solution solution = ComponentResolver.SOLUTIONS.get(type);
        if (solution == null || solution.fields == null) {
            if (solution == null) {
                solution = new Solution();
                ComponentResolver.SOLUTIONS.put(type, solution);
            }
            if (solution.fields == null) {
                // the resolve(...) method should never return
                // a null pointer, so it's never resolved again
                solution.fields = resolve(type);
            }
        }
        return solution.fields;
    }

    /**
     * Resolves all the injectable fields for the specified {@code type}.
     *
     * @param type The type to resolve
     * @return Returns all the injectable fields for
     * the specified {@code type}.
     */
    public List<InjectableField> resolve(TypeReference<?> type) {

        List<InjectableField> fields = new ArrayList<>();
        Class<?> clazz = type.getRawType();

        // Iterate all superclasses
        for (
                Class<?> checking = clazz;
                checking != null && checking != Object.class;
                checking = checking.getSuperclass()
        ) {
            // iterate all fields, including private fields
            // exclude fields that aren't annotated with
            // team.unnamed.inject.Inject
            boolean injectAll = checking.isAnnotationPresent(InjectAll.class);
            for (Field field : checking.getDeclaredFields()) {
                if (injectAll) {
                    if (
                            field.isSynthetic()
                                    || field.isAnnotationPresent(InjectIgnore.class)
                    ) {
                        continue;
                    }
                } else if (!field.isAnnotationPresent(Inject.class)) {
                    continue;
                }

                TypeReference<?> fieldType = type.getFieldType(field);
                InjectedKey<?> key = ComponentResolver.KEY_RESOLVER.keyOf(fieldType, field.getAnnotations());
                fields.add(new InjectableField(type, key, field));
            }

        }

        return fields;
    }

}
