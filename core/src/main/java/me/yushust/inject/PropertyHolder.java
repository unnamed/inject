package me.yushust.inject;

/**
 * Represents a property source, can be a loaded
 * .properties file, or any configuration file.
 *
 * <p>The properties are injected if annotated with
 * {@link Property}, these members must specify a
 * valid path. If the obtained value isn't assignable
 * to the annotated member, the error is attached
 * to the Injection stack and the error is reported
 * when the getInstance(...) or injectMembers(...) methods
 * ends.</p>
 */
public interface PropertyHolder {

  /** Gets the value for the specified property name */
  Object get(String property);

  /** Sets the value for the specified property name */
  void set(String property, Object value);

}
