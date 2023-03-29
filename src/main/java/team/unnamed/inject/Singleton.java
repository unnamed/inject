package team.unnamed.inject;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Scope
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Singleton {
}
