package team.unnamed.inject;


import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.FIELD;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({METHOD, CONSTRUCTOR, FIELD})
public @interface Inject {
}
