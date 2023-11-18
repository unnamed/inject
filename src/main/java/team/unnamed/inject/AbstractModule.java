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
package team.unnamed.inject;

import team.unnamed.inject.key.TypeReference;
import team.unnamed.inject.util.Validate;

public abstract class AbstractModule implements Module {

    private Binder binder;

    @Override
    public final void configure(Binder binder) {
        Validate.state(this.binder == null, "The binder is already being configured by this module!");
        this.binder = binder;
        configure();
        this.binder = null;
    }

    /**
     * The binder field isn't used directly because we need to
     * check if the binder is present, throwing the correct exception
     * instead of a simple and not descriptive null pointer exception
     *
     * @return The binder instance
     */
    protected final Binder binder() {
        Validate.state(binder != null, "The binder isn't specified yet!");
        return binder;
    }

    protected final <T> Binder.QualifiedBindingBuilder<T> bind(Class<T> keyType) {
        return binder().bind(keyType);
    }

    protected final <T> Binder.QualifiedBindingBuilder<T> bind(TypeReference<T> keyType) {
        return binder().bind(keyType);
    }

    protected final <T> Binder.MultiBindingBuilder<T> multibind(Class<T> keyType) {
        return binder().multibind(keyType);
    }

    protected final <T> Binder.MultiBindingBuilder<T> multibind(TypeReference<T> keyType) {
        return binder().multibind(keyType);
    }

    protected final void install(Module... modules) {
        binder().install(modules);
    }

    protected final void install(Iterable<? extends Module> modules) {
        binder().install(modules);
    }

    protected void configure() {
        // the method isn't abstract because
        // we don't want the user to implement
        // this method always, it can just use
        // provider methods
    }

}
