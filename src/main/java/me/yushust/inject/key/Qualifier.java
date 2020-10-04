package me.yushust.inject.key;

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

}
