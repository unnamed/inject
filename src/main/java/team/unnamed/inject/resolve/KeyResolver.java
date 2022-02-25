package team.unnamed.inject.resolve;

import team.unnamed.inject.assisted.Assist;
import team.unnamed.inject.impl.Annotations;
import team.unnamed.inject.key.InjectedKey;
import team.unnamed.inject.key.Key;
import team.unnamed.inject.key.TypeReference;

import javax.inject.Qualifier;
import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public final class KeyResolver {

    KeyResolver() {
    }

    /**
     * @return Resolves the key of the given parameter set and its annotations
     */
    public List<InjectedKey<?>> keysOf(
            TypeReference<?> declaringType,
            Parameter[] parameters
    ) {
        List<InjectedKey<?>> keys =
                new ArrayList<>(parameters.length);
        for (Parameter parameter : parameters) {
            Type type = parameter.getParameterizedType();
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
        Class<? extends Annotation> qualifierType = null;
        Annotation qualifier = null;

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
            if (
                    qualifierType == null
                            && qualifier == null
                            && annotationType.isAnnotationPresent(Qualifier.class)
            ) {
                if (Annotations.containsOnlyDefaultValues(annotation)) {
                    qualifierType = annotationType;
                } else {
                    qualifier = annotation;
                }
            }
        }

        Key<T> key = Key.of(type, qualifierType, qualifier);
        return new InjectedKey<>(key, optional, assisted);
    }

}
