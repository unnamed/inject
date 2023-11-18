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
package team.unnamed.inject.provision.std.generic;

import team.unnamed.inject.Provider;
import team.unnamed.inject.key.Key;

/**
 * It's (indirectly) a {@link Provider} for not-bound
 * parameterized types
 *
 * @param <T> The built type
 */
public interface GenericProvider<T> {

    /**
     * Creates an instance of {@link T} using
     * the provided type parameters.
     *
     * @param match The matched type
     * @return The created instance
     */
    T get(Key<?> match);

    /**
     * Converts this {@link GenericProvider} to a
     * constant normal {@link Provider} that always
     * use the given {@code match} key
     *
     * @param match The match key
     * @return The constant provider
     */
    default Provider<T> asConstantProvider(Key<?> match) {
        return () -> get(match);
    }

}
