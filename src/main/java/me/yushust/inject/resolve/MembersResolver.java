package me.yushust.inject.resolve;

import me.yushust.inject.key.TypeReference;

import java.util.List;

/**
 * Resolves all the members of an specific type.
 * Depending on implementation, the resolution of
 * members can be cached or not.
 *
 * <p>In case of a cached members box, the members
 * are resolved once and stored, then, the same
 * resolved members are returned</p>
 */
public interface MembersResolver {

  /**
   * @return Returns the first injectable constructor
   * found for the specified {@code type}.
   *
   * <p>If no constructor annotated with {@link javax.inject.Inject}
   * is found, the default/empty constructor is used (constructor
   * without parameters)</p>
   */
  InjectableConstructor getConstructor(TypeReference<?> type);

  /**
   * @return Returns all the injectable fields for
   * the specified {@code type}.
   */
  List<InjectableField> getFields(TypeReference<?> type);

  /**
   * @return Returns all the injectable methods for
   * the specified {@code type}.
   */
  List<InjectableMethod> getMethods(TypeReference<?> type);

}
