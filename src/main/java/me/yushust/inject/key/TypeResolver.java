package me.yushust.inject.key;

import java.lang.reflect.Type;

/**
 * Is a functional interface that resolves a
 * type that requires a context to be a
 * fully-specified type.
 *
 * @see CompositeType#requiresContext()
 */
public interface TypeResolver {

  Type resolveType(TypeReference<?> context, Type type);

}
