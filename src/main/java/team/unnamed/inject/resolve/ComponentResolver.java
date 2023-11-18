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

import team.unnamed.inject.key.TypeReference;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Resolves all the members of an specific type.
 * Depending on implementation, the resolution of
 * members can be cached or not.
 *
 * <p>In case of a cached members box, the members
 * are resolved once and stored, then, the same
 * resolved members are returned</p>
 */
public class ComponentResolver {

    static final KeyResolver KEY_RESOLVER
            = new KeyResolver();
    static final Map<TypeReference<?>, Solution> SOLUTIONS =
            new ConcurrentHashMap<>();
    private static final ConstructorResolver CONSTRUCTOR_RESOLVER
            = new ConstructorResolver();
    private static final FieldResolver FIELD_RESOLVER
            = new FieldResolver();
    private static final MethodResolver METHOD_RESOLVER
            = new MethodResolver();

    public static KeyResolver keys() {
        return KEY_RESOLVER;
    }

    public static ConstructorResolver constructor() {
        return CONSTRUCTOR_RESOLVER;
    }

    public static MethodResolver methods() {
        return METHOD_RESOLVER;
    }

    public static FieldResolver fields() {
        return FIELD_RESOLVER;
    }

}
