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
import team.unnamed.inject.error.ErrorAttachable;
import team.unnamed.inject.key.TypeReference;
import team.unnamed.inject.resolve.solution.InjectableConstructor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;

public final class ConstructorResolver {

    /**
     * Sentinel value for indicate that a constructor was not resolved yet
     */
    static final Object CONSTRUCTOR_NOT_DEFINED = new Object();

    ConstructorResolver() {
    }

    /**
     * Cached alternative method for {@link ConstructorResolver#resolve},
     * this method always uses the {@link Inject} annotation to resolve the constructors
     *
     * @param errors The error attachable
     * @param type The type to resolve
     * @return The injectable constructor
     */
    public InjectableConstructor get(
            ErrorAttachable errors,
            TypeReference<?> type
    ) {
        Solution solution = ComponentResolver.SOLUTIONS.get(type);
        // null constructor is valid and indicates that the constructor was
        // already resolved, the sentinel value indicates that the constructor
        // was never resolved!
        if (solution == null || solution.constructor == CONSTRUCTOR_NOT_DEFINED) {
            if (solution == null) {
                solution = new Solution();
                ComponentResolver.SOLUTIONS.put(type, solution);
            }
            solution.constructor = resolve(errors, type, Inject.class);
        }
        // so it's null or an instance of injectable constructor
        return (InjectableConstructor) solution.constructor;
    }

    /**
     * @return Returns the first injectable constructor
     * found for the specified {@code type}.
     *
     * <p>If no constructor annotated with the given {@code annotation}
     * is found, the default/empty constructor is used (constructor
     * without parameters)</p>
     *
     * @param errors The error attachable
     * @param type The type to resolve
     * @param annotation The annotation to search for
     * @return The injectable constructor
     */
    public InjectableConstructor resolve(
            ErrorAttachable errors,
            TypeReference<?> type,
            Class<? extends Annotation> annotation
    ) {

        Class<?> rawType = type.getRawType();

        Constructor<?> injectableConstructor = null;
        for (Constructor<?> constructor : rawType.getDeclaredConstructors()) {
            if (constructor.isAnnotationPresent(annotation)) {
                injectableConstructor = constructor;
                break;
            }
        }

        if (injectableConstructor == null) {
            try {
                injectableConstructor = rawType.getDeclaredConstructor();
            } catch (NoSuchMethodException ignored) {
            }
        }

        if (injectableConstructor == null) {
            errors.attach("No constructor found for type '" + type + "'");
            return null;
        }

        return new InjectableConstructor(
                ComponentResolver.keys().keysOf(
                        type,
                        injectableConstructor.getParameters()
                ),
                injectableConstructor
        );
    }

}
