package me.yushust.inject.resolve.solution;

import me.yushust.inject.impl.InjectorImpl;
import me.yushust.inject.impl.ProvisionStack;
import me.yushust.inject.key.TypeReference;

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
