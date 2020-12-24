package me.yushust.inject.resolve;

import me.yushust.inject.assisted.Assist;
import me.yushust.inject.key.InjectedKey;
import me.yushust.inject.key.Key;
import me.yushust.inject.key.Qualifier;
import me.yushust.inject.key.TypeReference;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class KeyResolver {

  KeyResolver() {
  }

  /**
   * Checks for annotations annotated with {@link javax.inject.Qualifier} and passes
   * the valid qualifiers to the qualifier factory, to convert the
   * annotations to a real {@link Qualifier}
   * @return The collection of valid found qualifiers
   */
  public Set<Qualifier> getQualifiers(Annotation[] annotations) {
    Set<Qualifier> qualifiers = new HashSet<>();
    for (Annotation annotation : annotations) {
      if (!annotation.annotationType().isAnnotationPresent(javax.inject.Qualifier.class)) {
        continue;
      }
      Qualifier qualifier = QualifierFactory.getQualifier(annotation);
      qualifiers.add(qualifier);
    }
    return qualifiers;
  }

  /** @return Resolves the key of the given parameter set and its annotations */
  public List<InjectedKey<?>> keysOf(
      TypeReference<?> declaringType,
      Parameter[] parameters
  ) {
    List<InjectedKey<?>> keys =
        new ArrayList<>(parameters.length);
    for (int i = 0; i < parameters.length; i++) {
      Parameter parameter = parameters[i];
      Type type = parameter.getType();
      Annotation[] annotations = parameter.getAnnotations();
      TypeReference<?> parameterType = declaringType.resolve(type);
      keys.add(keyOf(parameterType, annotations));
    }
    return keys;
  }

  public <T> InjectedKey<T> keyOf(
      TypeReference<T> type,
      Annotation[] annotations
  ) {
    boolean optional = false;
    boolean assisted = false;

    for (Annotation annotation : annotations) {
      Class<? extends Annotation> annotationType = annotation.annotationType();
      if (!optional) {
        String simpleName = annotationType.getSimpleName();
        // Please use "Nullable" instead of "nullable"
        if (simpleName.equalsIgnoreCase("Nullable")) {
          optional = true;
          continue;
        }
      }
      if (!assisted && annotationType == Assist.class) {
        assisted = true;
      }
    }

    Key<T> key = Key.of(type, getQualifiers(annotations));
    return new InjectedKey<>(key, optional, assisted);
  }

}
