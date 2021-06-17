package me.yushust.inject;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares that the annotated must
 * be bound to the type specified by
 * the {@link Targetted#value()} property.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Targetted {

	/** Returns the bound target class */
	Class<?> value();

}
