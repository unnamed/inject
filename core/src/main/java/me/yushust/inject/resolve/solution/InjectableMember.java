package me.yushust.inject.resolve.solution;

import me.yushust.inject.error.ErrorAttachable;
import me.yushust.inject.key.InjectedKey;
import me.yushust.inject.key.TypeReference;

import java.lang.reflect.Member;
import java.util.List;

/**
 * Represents an injectable member like a field,
 * method, etc.
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
   * {@link java.lang.reflect.Method}
   */
  Member getMember();

  /**
   * @return All the injections for this member, they
   * can be optional or not. If an injection isn't optional
   * and it cannot be injected, results in an error.
   * Order in keys is important!
   */
  List<InjectedKey<?>> getKeys();

  /**
   * Injects the values to this member in the specified {@code target}
   * If the injectable members is a field, the values array must contain
   * only one value. Else, the method results in a {@link IllegalArgumentException}
   * being thrown.
   *
   * @param values The injected values
   */
  Object inject(ErrorAttachable errors, Object target, Object[] values);

  /**
   * Converts the injectable member to a string that contains
   * relevant information. Try to return a pretty, short and
   * informative string like:
   *
   * <p>
   * Field 'fieldName' of type 'FieldType'
   * Method 'methodName(ParameterType, ParameterType2)'
   * </p>
   * <p>
   * Note that return type in methods doesn't matter, it won't be
   * injected.
   */
  @Override
  String toString();

}
