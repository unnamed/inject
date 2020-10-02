package me.yushust.inject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * X = Class annotated with this annotation
 * Y = value()
 *
 * Similar:
 * <p>
 *     Binder#bind(X.class).to(Y.class)
 * </p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ImplementedBy {

    /**
     * The implementation class
     * @return The implementation class
     */
    Class<?> value();

}
