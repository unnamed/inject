package me.yushust.inject;

import javax.inject.Qualifier;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * Represents an object that's obtained with
 * {@link PropertyHolder} specified by the user.
 * For example
 * <pre>
 *   &#64;Inject &#64;Property("my-property") private String property;
 * </pre>
 * is injected using {@link PropertyHolder#get(String)} passing
 * the property identifier/path/location
 *
 * <p>When a member is annotated with Property,
 * <strong>other annotations are ignored</strong></p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
@Target({FIELD, METHOD, PARAMETER})
@Documented
public @interface Property {

  /** The property identifier/path/location */
  String value();

}
