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

import team.unnamed.inject.Named;
import team.unnamed.inject.util.Validate;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Collection of factory static methods and other util
 * methods for ease the handling of qualifiers
 */
public final class Annotations {

    private Annotations() {
    }

    public static boolean containsOnlyDefaultValues(Annotation annotation) {

        for (Method method : annotation.annotationType().getDeclaredMethods()) {

            Object defaultValue = method.getDefaultValue();
            // no default value given
            if (defaultValue == null) {
                return false;
            }
            // try comparing to the actual value
            try {
                Object value = method.invoke(annotation);
                // if the actual value isn't equal to the default value
                if (!defaultValue.equals(value)) {
                    return false;
                }
            } catch (IllegalAccessException | InvocationTargetException ignored) {
            }
        }
        return true;
    }

    /**
     * Creates an instance of {@link Named} with
     * the specified {@code name} as value for
     * this annotation.
     *
     * @param name The name for the annotation
     * @return The instance of the annotation using
     * the specified {@code name}
     */
    public static Named createNamed(String name) {
        // not Validate.notEmpty(name), the name can be an empty string
        Validate.notNull(name);
        return new NamedImpl(name);
    }

    @SuppressWarnings("ClassExplicitlyAnnotation")
    private static class NamedImpl implements Named {

        private final String name;
        // the object will never change,
        // the hashCode can be cached
        private final int hashCode;

        private NamedImpl(String name) {
            this.name = name;
            this.hashCode = (127 * "value".hashCode()) ^ name.hashCode();
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return Named.class;
        }

        @Override
        public String value() {
            return name;
        }

        @Override
        public int hashCode() {
            return hashCode;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Named)) return false;
            return name.equals(((Named) obj).value());
        }

        @Override
        public String toString() {
            return "@Named(\"" + name + "\")";
        }

    }

}
