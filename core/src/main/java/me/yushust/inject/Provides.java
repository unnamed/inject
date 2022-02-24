package me.yushust.inject;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares that the annotated method
 * should be called as a provider. It's
 * automatically bound to its return type.
 *
 * <p>Provider methods can also receive
 * parameters, they will be obtained using
 * {@link Injector#getInstance} when required.</p>
 *
 * <p>Provider methods can also annotate its
 * return type and its parameters. Return value
 * annotations specify qualifiers and scopes.
 * Parameter annotations specify qualifiers.</p>
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Provides {

}
