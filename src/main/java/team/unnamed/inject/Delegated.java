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

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;

/**
 * Indicates that the bound type is a delegate
 * and it must be wrapped.
 * For example:
 * <pre>
 *   public interface MyHandler {
 *     // handle things...
 *   }
 *   public class MyDefaultHandler implements MyHandler {
 *     // implementations...
 *   }
 *   public class MyHandlerDecorator implements MyHandler {
 *     &#64;Inject &#64;Delegated private MyHandler delegate;
 *   }
 * </pre>
 * <p>
 * Configured as
 * <pre>
 *   // this is the delegated handler
 *   bind(MyHandler.class).markedWith(Delegated.class).to(MyDefaultHandler.class);
 *   // this is the decorator
 *   bind(MyHandler.class).to(MyHandlerDecorator.class);
 * </pre>
 * <p>
 * The annotation isn't used in the library, but it's
 * added here because many users of syringe (old trew)
 * added this annotation to its projects and used it
 * as specified.
 */
@Target({METHOD, FIELD, PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
@Documented
public @interface Delegated {

}
