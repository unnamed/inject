package me.yushust.inject.key;

import java.lang.reflect.TypeVariable;

/**
 * Represents a type formed by other types like
 * array types, parameterized types, wildcard types, etc.
 */
public interface CompositeType {

  /**
   * @return True if this composite type
   * contains a {@link TypeVariable}
   */
  boolean requiresContext();

}
