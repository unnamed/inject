package me.yushust.inject.multibinding;

import java.util.Map;

/**
 * Represents a map creator used by the
 * map multi-binder to inject its values
 */
public interface MapCreator {

  /** @return A new empty map */
  <K, V> Map<K, V> create();

}
