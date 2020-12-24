package me.yushust.inject;

import me.yushust.inject.property.PropertyHolder;

import javax.inject.Inject;
import java.util.Properties;

/**
 * Represents a Property Holder that delegates
 * the get and set methods to a {@link java.util.Properties},
 * to use this class as implementation for {@link PropertyHolder}
 * you just have to bind it.
 *
 * <pre>
 *   bind(Properties.class).to(...);
 *   bind(PropertyHolder.class).to(PropertiesHolder.class).singleton();
 * </pre>
 */
public class PropertiesHolder implements PropertyHolder {

  private final Properties properties;

  @Inject
  public PropertiesHolder(Properties properties) {
    this.properties = properties;
  }

  @Override
  public Object get(String property) {
    return properties.get(property);
  }

  @Override
  public void set(String property, Object value) {
    properties.put(property, value);
  }
}
