package me.yushust.inject.resolve;

import me.yushust.inject.key.TypeReference;
import me.yushust.inject.util.Validate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A cached wrapper for {@link MembersResolver}
 */
public class CachedMembersResolver implements MembersResolver {

  private final Map<TypeReference<?>, Solution> solutions =
      new ConcurrentHashMap<>();

  private final MembersResolver delegate;

  private CachedMembersResolver(MembersResolver delegate) {
    this.delegate = Validate.notNull(delegate, "delegate");
  }

  public InjectableConstructor getConstructor(TypeReference<?> type) {
    Solution solution = solutions.get(type);
    if (solution == null || solution.constructor == null) {
      if (solution == null) {
        solution = new Solution();
        solutions.put(type, solution);
      }
      // it should never return null, so we
      // are ok, if no constructor is found,
      // solution.constructor is always a
      // dummy injectable constructor, so the
      // constructor is never resolved again
      solution.constructor = delegate.getConstructor(type);
    }
    return solution.constructor;
  }

  public List<InjectableField> getFields(TypeReference<?> type) {
    Solution solution = solutions.get(type);
    if (solution == null || solution.fields == null) {
      if (solution == null) {
        solution = new Solution();
        solutions.put(type, solution);
      }
      if (solution.fields == null) {
        // the getFields(...) method should never return
        // a null pointer, so it's never resolved again
        solution.fields = delegate.getFields(type);
      }
    }
    return solution.fields;
  }

  public List<InjectableMethod> getMethods(TypeReference<?> type) {
    Solution solution = solutions.get(type);
    if (solution == null || solution.methods == null) {
      if (solution == null) {
        solution = new Solution();
        solutions.put(type, solution);
      }
      if (solution.methods == null) {
        // the getMethods(...) method should never return
        // a null pointer, so it's never resolved again
        solution.methods = delegate.getMethods(type);
      }
    }
    return solution.methods;
  }

  /** Represents an already resolved class */
  private static class Solution {
    private InjectableConstructor constructor;
    private List<InjectableField> fields;
    private List<InjectableMethod> methods;
  }

  /**
   * Wraps the provided {@code resolver} with the cache wrapper,
   * if the {@code resolver} is already an instance of {@link CachedMembersResolver},
   * the method returns the same resolver.s
   */
  public static MembersResolver wrap(MembersResolver resolver) {
    Validate.notNull(resolver, "resolver");
    if (resolver instanceof CachedMembersResolver) {
      return resolver;
    } else {
      return new CachedMembersResolver(resolver);
    }
  }

}
