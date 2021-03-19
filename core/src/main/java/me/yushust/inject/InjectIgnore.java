package me.yushust.inject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

/**
 * Indicates that the annotated {@link Field}
 * present in a type annotated with {@link InjectAll}
 * will be ignored, and, not injected.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface InjectIgnore {
}
