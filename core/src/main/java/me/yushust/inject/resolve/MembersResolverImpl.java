package me.yushust.inject.resolve;

import me.yushust.inject.Assist;
import me.yushust.inject.Qualifiers;
import me.yushust.inject.error.ErrorAttachable;
import me.yushust.inject.key.Key;
import me.yushust.inject.key.TypeReference;
import me.yushust.inject.util.Validate;

import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MembersResolverImpl implements MembersResolver {

  private final QualifierFactory qualifierFactory;

  public MembersResolverImpl(QualifierFactory qualifierFactory) {
    this.qualifierFactory = Validate.notNull(qualifierFactory, "qualifierFactory");
  }

  @Override
  public InjectableConstructor getConstructor(ErrorAttachable errors, TypeReference<?> type) {

    Constructor<?> injectableConstructor = null;
    for (Constructor<?> constructor : type.getRawType().getDeclaredConstructors()) {
      if (!constructor.isAnnotationPresent(Inject.class)) {
        continue;
      }
      injectableConstructor = constructor;
      break;
    }

    if (injectableConstructor == null) {
      try {
        injectableConstructor = type.getRawType().getDeclaredConstructor();
      } catch (NoSuchMethodException ignored) {
      }
    }

    if (injectableConstructor == null) {
      errors.attach("No constructor found for type '" + type + "'");
      return null;
    }

    return new InjectableConstructor(
        keysOf(
            type,
            injectableConstructor.getGenericParameterTypes(),
            injectableConstructor.getParameterAnnotations()
        ),
        injectableConstructor
    );
  }

  public List<InjectableField> getFields(TypeReference<?> type) {

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
        if (!field.isAnnotationPresent(Inject.class)
            && !field.isAnnotationPresent(Assist.class)) {
          continue;
        }
        TypeReference<?> fieldType = type.getFieldType(field);
        OptionalDefinedKey<?> key = keyOf(fieldType, field.getAnnotations());
        fields.add(new InjectableField(type, key, field));
      }
    }

    return fields;
  }

  @Override
  public List<InjectableMethod> getMethods(TypeReference<?> type, Class<? extends Annotation> annotation) {

    List<InjectableMethod> methods = new ArrayList<>();
    Class<?> clazz = type.getRawType();

    // Iterate all superclasses
    for (
        Class<?> checking = clazz;
        checking != null && checking != Object.class;
        checking = checking.getSuperclass()
    ) {
      // Iterate all methods, including private methods
      // exclude methods that aren't annotated with
      // javax.inject.Inject and add to the methods list,
      // not to the members list
      for (Method method : checking.getDeclaredMethods()) {
        if (!method.isAnnotationPresent(annotation)) {
          continue;
        }
        methods.add(
            new InjectableMethod(
                type,
                keysOf(
                    type,
                    method.getGenericParameterTypes(),
                    method.getParameterAnnotations()
                ),
                method
            )
        );
      }
    }

    return methods;
  }

  private List<OptionalDefinedKey<?>> keysOf(
      TypeReference<?> declaringType,
      Type[] parameterTypes,
      Annotation[][] parameterAnnotations
  ) {
    List<OptionalDefinedKey<?>> keys =
        new ArrayList<>();
    for (int i = 0; i < parameterTypes.length; i++) {
      Type parameter = parameterTypes[i];
      Annotation[] annotations = parameterAnnotations[i];
      TypeReference<?> parameterType = declaringType.resolve(parameter);
      keys.add(keyOf(parameterType, annotations));
    }
    return keys;
  }

  private OptionalDefinedKey<?> keyOf(TypeReference<?> type, Annotation[] annotations) {
    boolean optional = false;
    boolean assisted = false;
    for (Annotation annotation : annotations) {
      Class<? extends Annotation> annotationType = annotation.annotationType();
      if (!optional) {
        String simpleName = annotationType.getSimpleName();
        // Please use "Nullable" instead of "nullable"
        if (simpleName.equalsIgnoreCase("Nullable")) {
          optional = true;
        }
      }
      if (!assisted && annotationType == Assist.class) {
        assisted = true;
      }
    }
    @SuppressWarnings({"rawtypes"})
    Key key = Key.of(type, Qualifiers.getQualifiers(qualifierFactory, annotations));
    @SuppressWarnings("unchecked")
    OptionalDefinedKey<?> optionalDefinedKey =
        new OptionalDefinedKey<Object>(key, optional, assisted);
    return optionalDefinedKey;
  }

}
