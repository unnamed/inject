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
import team.unnamed.inject.key.TypeReference;
import team.unnamed.inject.resolve.solution.InjectableMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public final class MethodResolver {

    MethodResolver() {
    }

    /**
     * Cached alternative method for {@link MethodResolver#resolve}
     * that always uses  the {@link Inject} annotation to resolve the
     * methods.
     *
     * @param type The type to resolve
     * @return The injectable methods
     */
    public List<InjectableMethod> get(
            TypeReference<?> type
    ) {
        Solution solution = ComponentResolver.SOLUTIONS.get(type);
        if (solution == null || solution.methods == null) {
            if (solution == null) {
                solution = new Solution();
                ComponentResolver.SOLUTIONS.put(type, solution);
            }
            if (solution.methods == null) {
                // the resolve(...) method should never return
                // a null pointer, so it's never resolved again
                solution.methods = resolve(type, Inject.class);
            }
        }
        return solution.methods;
    }

    /**
     * @param type The type to resolve
     * @param annotation The annotation to resolve
     * @return Returns all the injectable methods for
     * the specified {@code type}. The resolved methods
     * can also be used to get the module provider methods
     */
    public List<InjectableMethod> resolve(
            TypeReference<?> type,
            Class<? extends Annotation> annotation
    ) {

        List<InjectableMethod> methods = new ArrayList<>();
        Class<?> clazz = type.getRawType();

        // Iterate all superclasses
        for (
                Class<?> checking = clazz;
                checking != null && checking != Object.class;
                checking = checking.getSuperclass()
        ) {
            // Iterate all methods, including private methods
            // exclude methods that aren't annotated with
            // @Inject and add to the methods list,
            // not to the members list
            for (Method method : checking.getDeclaredMethods()) {
                if (!method.isAnnotationPresent(annotation)) {
                    continue;
                }
                methods.add(
                        new InjectableMethod(
                                type,
                                ComponentResolver.KEY_RESOLVER.keysOf(
                                        type,
                                        method.getParameters()
                                ),
                                method
                        )
                );
            }
        }

        return methods;
    }

}
