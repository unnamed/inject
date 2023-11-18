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

import team.unnamed.inject.Provider;

/**
 * Collection of built-in scopes
 */
public final class Scopes {

    public static final Scope SINGLETON
            = new LazySingletonScope();

    public static final Scope NONE
            = EmptyScope.INSTANCE;

    private static final ScopeScanner SCANNER
            = new ScopeScanner();

    private Scopes() {
    }

    /**
     * Returns the scope scanner instance
     */
    public static ScopeScanner getScanner() {
        return SCANNER;
    }

    /**
     * Represents an scope that always returns the
     * same unscoped provider. The implementation is
     * an enum to let the JVM make sure only one
     * instance exists.
     */
    private enum EmptyScope implements Scope {
        INSTANCE;

        @Override
        public <T> Provider<T> scope(Provider<T> unscoped) {
            return unscoped;
        }

        @Override
        public String toString() {
            return "EmptỳScope";
        }
    }

}
