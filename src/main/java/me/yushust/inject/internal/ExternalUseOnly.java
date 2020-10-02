package me.yushust.inject.internal;

import java.lang.annotation.*;

/**
 * Like {@link ThreadSensitive}, this is only
 * a marker annotation to make the code more legible,
 * and to ease following some constraints and helping
 * with the application design.
 *
 * <p>This annotation indicates that the annotated
 * method must be called only for users, in other words,
 * internal methods mustn't call the annotated method</p>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExternalUseOnly {
}
