package me.yushust.inject.key.resolve;

import me.yushust.inject.key.CompositeType;
import me.yushust.inject.key.TypeReference;
import me.yushust.inject.key.TypeResolver;
import me.yushust.inject.key.Types;
import me.yushust.inject.util.Validate;

import java.lang.reflect.*;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Collection of util methods for contextually
 * handling of types.
 */
public final class ContextualTypes {

  private static final Map<Class<?>, TypeResolver> TYPE_RESOLVER_MAP;

  static {
    Map<Class<?>, TypeResolver> modifiableTypeResolvers =
        new LinkedHashMap<Class<?>, TypeResolver>();

    modifiableTypeResolvers.put(TypeVariable.class, new TypeVariableResolver());
    modifiableTypeResolvers.put(GenericArrayType.class, new GenericArrayResolver());
    modifiableTypeResolvers.put(ParameterizedType.class, new ParameterizedTypeResolver());
    modifiableTypeResolvers.put(WildcardType.class, new WildcardTypeResolver());

    TYPE_RESOLVER_MAP = Collections.unmodifiableMap(modifiableTypeResolvers);
  }

  private ContextualTypes() {
    throw new UnsupportedOperationException("This class couldn't be instantiated!");
  }

  /**
   * Checks if the given type requires a context to be
   * fully specified.
   *
   * @param type The checked type.
   * @return True if the type requires a context to
   * be fully specified.
   */
  public static boolean requiresContext(Type type) {

    Validate.notNull(type, "type");

    if (type instanceof Class) {
      return false;
    } else if (type instanceof CompositeType) {
      return ((CompositeType) type).requiresContext();
    } else if (type instanceof TypeVariable) {
      return true;
    } else {
      Type wrapped = Types.wrap(type);
      Validate.state(wrapped instanceof CompositeType); // wtf bro this isn't possible
      return ((CompositeType) wrapped).requiresContext();
    }
  }

  public static Type getSupertype(Type type, Class<?> rawType, Class<?> resolvingType) {

    Validate.notNull(type, "type");
    Validate.notNull(rawType, "rawType");
    Validate.notNull(resolvingType, "resolvingType");

    if (resolvingType == rawType) {
      return type;
    }

    if (resolvingType.isInterface()) {
      Class<?>[] rawInterfaceTypes = rawType.getInterfaces();

      for (int i = 0; i < rawInterfaceTypes.length; i++) {
        Class<?> rawInterfaceType = rawInterfaceTypes[i];
        Type interfaceType = rawType.getGenericInterfaces()[i];
        if (rawInterfaceType == resolvingType) {
          return interfaceType;
        }
        if (resolvingType.isAssignableFrom(rawInterfaceType)) {
          return getSupertype(interfaceType, rawInterfaceType, resolvingType);
        }
      }
    }

    if (rawType.isInterface() || rawType == Object.class) {
      return resolvingType;
    }

    for (
        Class<?> rawSupertype = rawType.getSuperclass();
        rawType != Object.class;
        rawType = (rawSupertype = rawType.getSuperclass())
    ) {
      if (rawSupertype == resolvingType) {
        return rawType.getGenericSuperclass();
      }
      if (resolvingType.isAssignableFrom(rawSupertype)) {
        return getSupertype(rawType.getGenericSuperclass(), rawSupertype, resolvingType);
      }
    }

    return resolvingType;

  }

  /**
   * Resolves the type contextually.
   *
   * @param context The context
   * @param type    The possibly non-fully-specified type
   * @return A fully specified type
   */
  public static Type resolveContextually(TypeReference<?> context, Type type) {

    Validate.notNull(context);

    for (Map.Entry<Class<?>, TypeResolver> typeResolverEntry : TYPE_RESOLVER_MAP.entrySet()) {

      Class<?> clazz = typeResolverEntry.getKey();
      TypeResolver typeResolver = typeResolverEntry.getValue();

      if (clazz.isInstance(type)) {
        return typeResolver.resolveType(context, type);
      }
    }

    return type;
  }

}
