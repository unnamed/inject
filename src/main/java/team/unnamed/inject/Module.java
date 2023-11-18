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

/**
 * This interface represents a part of the {@link Injector}
 * configuration. A module is usually used to bind abstractions
 * (interfaces, abstract classes) to implementations (concrete
 * classes). To just add a scope to a class, you can use scope
 * annotations like {@link Singleton}.
 * You can also use annotations to bind abstractions to implementations
 * like {@link Targetted} and {@link ProvidedBy}.
 *
 * <p>Read about the embedded dsl to configure injector in {@link Binder}.</p>
 *
 * <p>You can also create provider methods (methods used as providers) and
 * annotate it with {@link Provides}. Annotate the method with
 * {@link Inject} if the object creation has dependencies.</p>
 *
 * <p>There's an extension for users to create bindings in a pretty way
 * using {@link AbstractModule}.</p>
 *
 * @see Binder Read the EDSL specification
 */
public interface Module {

    /**
     * Configures all bindings on the specified {@code binder}. This method
     * is called before checking for methods annotated with {@literal @}{@link Provides}
     * to create method-based providers.
     *
     * <p>Don't invoke this method to configure binders. Instead use {@link Binder#install}
     * to check all the methods annotated with {@literal @}{@link Provides}</p>
     *
     * @param binder The configuring binder, attached to an specific injector
     */
    void configure(Binder binder);

}
