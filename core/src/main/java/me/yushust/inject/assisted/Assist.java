package me.yushust.inject.assisted;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;

/**
 * Declares that the annotated parameter
 * will be assisted
 */
@Target({PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Assist {
}
