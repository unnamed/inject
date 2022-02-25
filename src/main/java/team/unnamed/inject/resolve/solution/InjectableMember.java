package team.unnamed.inject.resolve.solution;

import team.unnamed.inject.impl.InjectorImpl;
import team.unnamed.inject.impl.ProvisionStack;
import team.unnamed.inject.key.TypeReference;

import java.lang.reflect.Member;

/**
 * Represents an injectable member like a field,
 * method or constructor.
 */
public interface InjectableMember {

    /**
     * @return The declaring raw or generic
     * type of this injectable member.
     */
    TypeReference<?> getDeclaringType();

    /**
     * @return The injected member, for fields,
     * an instance of {@link java.lang.reflect.Field},
     * for methods, an instance of
     * {@link java.lang.reflect.Method}, for
     * constructors, a {@link java.lang.reflect.Constructor}
     */
    Member getMember();

    /**
     * Gets and injects the required keys in the
     * specified {@code target}
     */
    Object inject(InjectorImpl injector, ProvisionStack stack, Object target);

}
