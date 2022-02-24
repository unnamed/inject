package me.yushust.inject.resolve;

import me.yushust.inject.key.TypeReference;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Resolves all the members of an specific type.
 * Depending on implementation, the resolution of
 * members can be cached or not.
 *
 * <p>In case of a cached members box, the members
 * are resolved once and stored, then, the same
 * resolved members are returned</p>
 */
public class ComponentResolver {

    static final KeyResolver KEY_RESOLVER
            = new KeyResolver();
    static final Map<TypeReference<?>, Solution> SOLUTIONS =
            new ConcurrentHashMap<>();
    private static final ConstructorResolver CONSTRUCTOR_RESOLVER
            = new ConstructorResolver();
    private static final FieldResolver FIELD_RESOLVER
            = new FieldResolver();
    private static final MethodResolver METHOD_RESOLVER
            = new MethodResolver();

    public static KeyResolver keys() {
        return KEY_RESOLVER;
    }

    public static ConstructorResolver constructor() {
        return CONSTRUCTOR_RESOLVER;
    }

    public static MethodResolver methods() {
        return METHOD_RESOLVER;
    }

    public static FieldResolver fields() {
        return FIELD_RESOLVER;
    }

}
