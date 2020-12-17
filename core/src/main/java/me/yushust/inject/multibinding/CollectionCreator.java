package me.yushust.inject.multibinding;

import java.util.Collection;

/**
 * Represents a collection creator used by
 * the collection multi-binder to inject its
 * values
 */
public interface CollectionCreator {

  /** @return A new empty collection */
  <E> Collection<E> create();

}
