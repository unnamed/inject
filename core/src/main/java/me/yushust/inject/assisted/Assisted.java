package me.yushust.inject.assisted;

import java.lang.annotation.*;

/**
 * Declares that the annotated constructor
 * has assisted injections
 * @see Assist
 */
@Target(ElementType.CONSTRUCTOR)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Assisted {
}
