package me.yushust.inject.key;

/**
 * Represents a {@link Key} qualifier,
 * like an annotation
 */
public interface Qualifier {

  /**
   * {@inheritDoc}
   *
   * @return The qualifier converted to a
   * string, this string must be short and
   * descriptive, add just relevant information
   * to this string, add also a predicate like
   * <p>annotated with @Named("name")</p>
   * <p>marked with @Marker</p>
   * <p>annotated with @Annotation(value = "param", key = 123)</p>
   */
  @Override
  String toString();

  /**
   * {@inheritDoc}
   *
   * @return The hashCode of this qualifier,
   * the hashCode must be overwritten (don't use
   * default identityHashCode of Object).
   *
   * The hashCode must follow the hashCode()
   * method contracts.
   * Read https://www.baeldung.com/java-equals-hashcode-contracts
   */
  @Override
  int hashCode();

  /**
   * {@inheritDoc}
   *
   * @return Like {@link Object#hashCode()},
   * this method must be overwritten (don't use
   * the sole identity comparison)
   *
   * The equals method must follow the equals()
   * method contracts.
   * Read https://www.baeldung.com/java-equals-hashcode-contracts
   */
  @Override
  boolean equals(Object o);

}
