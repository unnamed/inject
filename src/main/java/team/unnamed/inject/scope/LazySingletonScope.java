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
import team.unnamed.inject.provision.StdProvider;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A lazy singleton provider wrapper
 */
public final class LazySingletonScope
        implements Scope {

    @Override
    public <T> Provider<T> scope(Provider<T> unscoped) {
        // the provider is already scoped
        if (unscoped instanceof LazySingletonProvider) {
            return unscoped;
        } else {
            return new LazySingletonProvider<>(unscoped);
        }
    }

    /**
     * Singleton provider. Singleton instance is instantiated
     * using double-checked-locking with ReentrantLock.
     * We use a volatile reference for more thread-safety
     *
     * <p>The singleton provider wrapper executes the delegated
     * provider once, then, returns the same saved instance.</p>
     *
     * <p>The singleton provider extends to {@link StdProvider}
     * only for optimization, the injection is never invoked in
     * the SingletonProvider, it's invoked in the delegate.</p>
     *
     * @param <T> The provided type
     */
    static class LazySingletonProvider<T>
            implements Provider<T> {

        private final Lock instanceLock = new ReentrantLock();
        private final Provider<T> delegate;
        /**
         * Volatile reference to saved instance. Initially null,
         * we double-check if the instance is null
         */
        private volatile T instance;

        /**
         * Constructs a new Singleton Provider wrapper
         *
         * @param unscoped The unscoped provider
         */
        LazySingletonProvider(Provider<T> unscoped) {
            this.delegate = unscoped;
        }

        @Override
        public T get() {
            // non-synchronized check
            if (instance == null) {
                instanceLock.lock();
                try {
                    if (instance == null) { // synchronized check
                        instance = delegate.get();
                    }
                } finally { // important to release the lock in a finally block
                    instanceLock.unlock();
                }
            }

            return instance;
        }

        @Override
        public String toString() {
            return "LazySingleton(" + delegate.toString() + ")";
        }

    }

}
