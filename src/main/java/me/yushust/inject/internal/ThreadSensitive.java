package me.yushust.inject.internal;

import java.lang.annotation.*;

/**
 * A marker annotation to add a bit of detail, making
 * the code more legible.
 *
 * <p>Indicates that the annotated method is sensitive
 * to the current thread, for example a method that
 * uses a ThreadLocal, a method that interacts directly
 * with the thread, etc.</p>
 *
 * <p>I prefer not to annotate all methods that delegates
 * the functionality to another overloaded methods, so I
 * only annotate the overloaded methods with the main
 * functionality</p>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
@Documented
public @interface ThreadSensitive {
}
