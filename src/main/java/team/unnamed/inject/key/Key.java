package team.unnamed.inject.key;

import team.unnamed.inject.key.Types.CompositeType;
import team.unnamed.inject.util.ElementFormatter;
import team.unnamed.inject.util.Validate;

import javax.inject.Qualifier;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.Objects;
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

    // The qualifier type, used when
    // classes are used as qualifiers,
    // its value can be null
    private final Class<? extends Annotation> qualifierType;

    // The qualifier instance, used
    // when instances are used as qualifiers,
    // its value can be null
    private final Annotation qualifier;

    // This class is an immutable class, so
    // we can cache the hashcode and optimize
    // a bit the hashCode() method
    private final int hashCode;

    public Key(
            TypeReference<T> type,
            Class<? extends Annotation> qualifierType,
            Annotation qualifier
    ) {
        Validate.notNull(type, "type");
        Validate.argument(
                !(qualifierType != null && qualifier != null),
                "Cannot use both qualifierType and qualifier qualifiers!"
        );
        this.type = type.canonicalize();
        this.qualifierType = qualifierType;
        this.qualifier = qualifier;
        this.hashCode = computeHashCode();
    }

    public static <T> Key<T> of(Class<T> type) {
        return of(TypeReference.of(type));
    }

    public static <T> Key<T> of(TypeReference<T> type) {
        return new Key<>(type, null, null);
    }

    public static <T> Key<T> of(
            TypeReference<T> type,
            Class<? extends Annotation> qualifierType,
            Annotation qualifier
    ) {
        return new Key<>(type, qualifierType, qualifier);
    }

    /**
     * Determines if the {@link Key} represented by this is a raw-type
     */
    public boolean isPureRawType() {
        return type.isPureRawType();
    }

    /**
     * Checks if the wrapped type requires context or not
     */
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
     * Returns the qualifier instance attached
     * to this type key
     */
    public Annotation getQualifier() {
        return qualifier;
    }

    /**
     * Returns the qualifier type attached
     * to this type key
     */
    public Class<? extends Annotation> getQualifierType() {
        return qualifierType;
    }

    /**
     * Returns a new {@link Key} instance with the
     * given type, the qualifier is copied
     *
     * @param type The new key type
     * @param <R> The new key type
     * @return The new key
     */
    public <R> Key<R> withType(TypeReference<R> type) {
        return new Key<>(type, qualifierType, qualifier);
    }

    /**
     * Returns a new {@link Key} with the given {@code qualifier}
     */
    public Key<T> withQualifier(Annotation qualifier) {
        return new Key<>(type, null, qualifier);
    }

    /**
     * Returns a new {@link Key} with the given {@code qualifierType}
     */
    public Key<T> withQualifier(Class<? extends Annotation> qualifierType) {
        return new Key<>(type, qualifierType, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Key)) return false;
        Key<?> key = (Key<?>) o;
        return hashCode == key.hashCode &&
                type.equals(key.type) &&
                Objects.equals(qualifier, key.qualifier) &&
                Objects.equals(qualifierType, key.qualifierType);
    }

    private int computeHashCode() {
        return Objects.hash(type, qualifier, qualifierType);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    /**
     * This should create a string with util information
     * and very verbose. Like
     *
     * <pre>
     *   team.unnamed.inject.ExampleType
     *     annotated with @Named("hello")
     *     marked with @Marker
     * </pre>
     *
     * If a class name starts with {@code java} or {@code javax},
     * it isn't used, the used name is now {@link Class#getSimpleName()}
     *
     * @return The key information as string
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(type.toString());
        if (qualifierType != null) {
            builder.append(" marked with @")
                    .append(qualifierType.getSimpleName());
        } else if (qualifier != null) {
            builder.append(" annotated with ")
                    .append(ElementFormatter.annotationToString(qualifier));
        }
        return builder.toString();
    }

}
