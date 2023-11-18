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
package team.unnamed.inject.resolve.solution;

import team.unnamed.inject.impl.InjectorImpl;
import team.unnamed.inject.impl.ProvisionStack;
import team.unnamed.inject.key.TypeReference;

import java.lang.reflect.Member;

/**
 * Represents an injectable member like a field,
 * method or constructor.
 */
public interface InjectableMember {

    /**
     * @return The declaring raw or generic
     * type of this injectable member.
     */
    TypeReference<?> getDeclaringType();

    /**
     * @return The injected member, for fields,
     * an instance of {@link java.lang.reflect.Field},
     * for methods, an instance of
     * {@link java.lang.reflect.Method}, for
     * constructors, a {@link java.lang.reflect.Constructor}
     */
    Member getMember();

    /**
     * Gets and injects the required keys in the
     * specified {@code target}
     *
     * @param injector The injector instance
     * @param stack The provision stack
     * @param target The target instance
     * @return The injected instance
     */
    Object inject(InjectorImpl injector, ProvisionStack stack, Object target);

}
