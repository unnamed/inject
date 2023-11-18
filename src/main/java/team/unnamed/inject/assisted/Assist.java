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
package team.unnamed.inject.assisted;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;

/**
 * Declares that the annotated parameter isn't provided
 * by the injector, it's provided by the class creator.
 * Without assisted inject, we use things like
 * <pre>
 *   public class PartyCreator {
 *     &#64;Inject private SomeDependency dependency;
 *
 *     public Party createParty(Member[] members) {
 *       return new Party(dependency, members);
 *     }
 *   }
 * </pre>
 * because we can't inject {@code members} here.
 * <p>
 * So we can use Assisted Inject!
 * <pre>
 *   public class Party {
 *     private Member[] members;
 *
 *     &#64;Assisted
 *     public Party(
 *       &#64;Assist Member[] members,
 *       SomeDependency dependency
 *     ) {
 *       // your code...
 *     }
 *
 *     // your code...
 *   }
 * </pre>
 * And its factory (it must be an interface, you don't need to
 * create the implementation, it's actually created using proxies)
 * <pre>
 *   public interface PartyFactory {
 *     Party create(Member[] members);
 *   }
 * </pre>
 * The we bind it
 * <pre>
 *   bind(Party.class).toFactory(PartyFactory.class);
 * </pre>
 * And then we use it
 * <pre>
 *   PartyFactory factory = injector.getInstance(PartyFactory.class);
 *   Party party = factory.create(members);
 * </pre>
 */
@Target({PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Assist {

}
