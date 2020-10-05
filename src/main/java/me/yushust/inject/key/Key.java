package me.yushust.inject.key;

import me.yushust.inject.util.Validate;
import me.yushust.inject.key.Types.CompositeType;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a binding key used to store a relation of Key -> Provider.
 * Holds a {@link TypeReference} representing the type of the key,
 * and a {@link Set} of {@link Qualifier} representing the annotations or
 * annotation types that works like qualifiers.
 *
 * <p>The key supports generic types using {@link TypeReference}, the key
 * cannot be created using a sub-class like {@link TypeReference}.</p>
 *
 * @param <T> The type of the key
 */
public final class Key<T> implements CompositeType, Serializable {

  private static final long serialVersionUID = 987654321L;

  // The generic or raw type reference
  private final TypeReference<T> type;
  // The unmodifiable set of qualifiers,
  // never null. It's an empty set if no
  // qualifiers are required
  private final Set<Qualifier> qualifiers;

  // This class is an immutable class, so
  // we can cache the hashcode and optimize
  // a bit the hashCode() method
  private final int hashCode;
  // Same for toString() method
  private final String toString;

  public Key(TypeReference<T> type, Set<Qualifier> qualifiers) {
    Validate.notNull(type, "type");
    Validate.notNull(qualifiers, "qualifiers");
    this.type = type.canonicalize();
    this.qualifiers = Collections.unmodifiableSet(qualifiers);
    this.hashCode = computeHashCode();
    this.toString = computeToString();
  }

  /** Checks if the wrapped type requires context or not */
  public boolean requiresContext() {
    // delegate functionality to TypeReference
    return type.requiresContext();
  }

  /**
   * @return The generic or raw type of the key
   */
  public TypeReference<T> getType() {
    return type;
  }

  /**
   * @return An immutable set that contains
   * all the qualifiers of this key
   */
  public Set<Qualifier> getQualifiers() {
    return qualifiers;
  }

  /**
   * @return A key with the same type but
   * with no qualifiers
   */
  public Key<T> withNoQualifiers() {
    return new Key<>(type, Collections.emptySet());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Key)) {
      return false;
    }
    Key<?> key = (Key<?>) o;
    return hashCode == key.hashCode &&
        type.equals(key.type) &&
        qualifiers.equals(key.qualifiers);
  }

  private int computeHashCode() {
    // hashCode following the hashCode contracts
    int result = 1;
    result = 31 * result + type.hashCode();
    result = 31 * result + qualifiers.hashCode();
    return result;
  }

  @Override
  public int hashCode() {
    return hashCode;
  }

  private String computeToString() {
    StringBuilder builder = new StringBuilder(type.toString());
    for (Qualifier qualifier : qualifiers) {
      builder.append("\n");
      builder.append("    ");
      builder.append(qualifier);
    }
    return builder.toString();
  }

  /**
   * This should create a string with util information
   * and very verbose. Like
   * <p>
   * me.yushust.inject.ExampleType
   * annotated with @Named("hello")
   * marked with @Marker
   * </p>
   * If a class name starts with {@code java} or {@code javax},
   * it isn't used, the used name is now {@link Class#getSimpleName()}
   *
   * @return The key information as string
   */
  @Override
  public String toString() {
    return toString;
  }

  public static <T> Key<T> of(TypeReference<T> type) {
    return new Key<>(type, Collections.emptySet());
  }

  public static <T> Key<T> of(TypeReference<T> type, Iterable<? extends Qualifier> qualifiers) {
    Set<Qualifier> qualifierSet = new HashSet<>();
    for (Qualifier qualifier : qualifiers) {
      qualifierSet.add(qualifier);
    }
    return new Key<>(type, qualifierSet);
  }

}
