package me.yushust.inject.assisted;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;

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
 *
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
