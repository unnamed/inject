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
package team.unnamed.inject.scope;

import team.unnamed.inject.Singleton;
import team.unnamed.inject.util.Validate;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.HashMap;
import java.util.Map;

/**
 * Class responsible of scanning
 * scope annotations from {@link AnnotatedElement}
 */
public final class ScopeScanner {

    private final Map<Class<? extends Annotation>, Scope> scopes
            = new HashMap<>();

    ScopeScanner() {
        scopes.put(Singleton.class, Scopes.SINGLETON);
    }

    /**
     * Binds the given {@code annotationType} to
     * the specified {@code scope} instance.
     *
     * <p>Note that this method doesn't require
     * the {@code annotationType} to be annotated
     * with {@link team.unnamed.inject.Scope}</p>
     */
    public void bind(Class<? extends Annotation> annotationType, Scope scope) {
        Validate.notNull(annotationType, "annotationType");
        Validate.notNull(scope, "scope");
        scopes.put(annotationType, scope);
    }

    /**
     * Scans the given {@code element} annotations searching
     * for annotations present in the internal {@code scopes}
     * map
     *
     * @return The found scope, {@link Scopes#NONE} if no scopes
     * found
     */
    public Scope scan(AnnotatedElement element) {
        Annotation[] annotations = element.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            Class<? extends Annotation> annotationType = annotation.annotationType();
            Scope scope = scopes.get(annotationType);
            if (scope != null) {
                return scope;
            }
        }
        return Scopes.NONE;
    }

}
