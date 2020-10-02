package me.yushust.inject.key;

import me.yushust.inject.util.Validate;

import java.lang.reflect.*;

public class TypeReference<T> {

  private final Class<? super T> rawType;
  private final Type type;

  @SuppressWarnings("unchecked")
  protected TypeReference() {

    Type superClass = getClass().getGenericSuperclass();

    Validate.state(superClass instanceof ParameterizedType,
        "Invalid TypeReference creation.");

    ParameterizedType parameterized = (ParameterizedType) superClass;

    this.type = Types.wrap(parameterized.getActualTypeArguments()[0]);
    this.rawType = (Class<? super T>) Types.getRawType(type);
  }

  @SuppressWarnings("unchecked")
  public TypeReference(Type type) {
    Validate.notNull(type);
    this.type = Types.wrap(type);
    this.rawType = (Class<? super T>) Types.getRawType(this.type);
  }

  private TypeReference(Type type, Class<? super T> rawType) {
    Validate.notNull(type, "type");
    Validate.notNull(rawType, "rawType");
    this.type = type;
    this.rawType = rawType;
  }

  public final Class<? super T> getRawType() {
    return rawType;
  }

  public final Type getType() {
    return type;
  }

  /**
   * Removes the reference for the upper class. For
   * anonymous classes. If you store a {@link TypeReference}
   * in cache, you should execute this method before.
   *
   * @return The type reference.
   */
  public final TypeReference<T> canonicalize() {
    // This object isn't an instance of
    // an anonymous class
    if (getClass() == TypeReference.class) {
      return this;
    } else {
      return new TypeReference<T>(type, rawType);
    }
  }

  @Override
  public final int hashCode() {
    return type.hashCode();
  }

  @Override
  public final boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof TypeReference<?>)) {
      return false;
    }

    TypeReference<?> other = (TypeReference<?>) o;
    return Types.typeEquals(type, other.type);
  }

  @Override
  public final String toString() {
    return Types.asString(type);
  }

  @Override
  protected final Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  @Override
  protected final void finalize() throws Throwable {
    super.finalize();
  }

  public static <T> TypeReference<T> of(Type type) {
    return new TypeReference<T>(type);
  }

  public static <T> TypeReference<T> of(Type rawType, Type... typeArguments) {
    Validate.notNull(rawType);
    return of(new ParameterizedTypeReference(null, rawType, typeArguments));
  }

}