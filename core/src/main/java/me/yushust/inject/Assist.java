package me.yushust.inject;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;

/**
 * Declares that the annotated injected member
 * will be assisted, its bindings are checked
 * only in the given JIT bindings map.
 */
@Target({FIELD, PARAMETER, METHOD, CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Assist {
}
