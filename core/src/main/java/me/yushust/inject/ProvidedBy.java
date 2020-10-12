package me.yushust.inject;

import javax.inject.Provider;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * X = Class annotated with this annotation
 * Y = value()
 * Similar:
 * <p>
 * Binder#bind(X.class).toProvider(Y.class)
 * </p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ProvidedBy {

  /**
   * The provider class
   *
   * @return The provider class
   */
  Class<? extends Provider<?>> value();

}
