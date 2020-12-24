package me.yushust.inject.resolve;

import me.yushust.inject.key.InjectedKey;
import me.yushust.inject.key.TypeReference;
import me.yushust.inject.resolve.solution.InjectableField;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public final class FieldResolver {

  FieldResolver() {
  }

  /** Cached alternative method for {@link FieldResolver#resolve}*/
  public List<InjectableField> get(TypeReference<?> type) {
    Solution solution = ComponentResolver.SOLUTIONS.get(type);
    if (solution == null || solution.fields == null) {
      if (solution == null) {
        solution = new Solution();
        ComponentResolver.SOLUTIONS.put(type, solution);
      }
      if (solution.fields == null) {
        // the resolve(...) method should never return
        // a null pointer, so it's never resolved again
        solution.fields = resolve(type);
      }
    }
    return solution.fields;
  }

  /**
   * @return Returns all the injectable fields for
   * the specified {@code type}.
   */
  public List<InjectableField> resolve(TypeReference<?> type) {

    List<InjectableField> fields = new ArrayList<>();
    Class<?> clazz = type.getRawType();

    // Iterate all superclasses
    for (
        Class<?> checking = clazz;
        checking != null && checking != Object.class;
        checking = checking.getSuperclass()
    ) {
      // iterate all fields, including private fields
      // exclude fields that aren't annotated with
      // javax.inject.Inject
      for (Field field : checking.getDeclaredFields()) {
        if (!field.isAnnotationPresent(Inject.class)) {
          continue;
        }
        TypeReference<?> fieldType = type.getFieldType(field);
        InjectedKey<?> key = ComponentResolver.KEY_RESOLVER.keyOf(fieldType, field.getAnnotations());
        fields.add(new InjectableField(type, key, field));
      }
    }

    return fields;
  }

}
