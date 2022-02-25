package team.unnamed.inject;

import javax.inject.Provider;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Similar to {@link Targetted}, but the
 * annotated type is bound to a provider
 * type, not to a sub-class.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ProvidedBy {

    /**
     * Returns the bound provider
     */
    Class<? extends Provider<?>> value();

}
