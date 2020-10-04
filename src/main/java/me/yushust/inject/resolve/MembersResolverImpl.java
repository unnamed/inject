package me.yushust.inject.resolve;

import me.yushust.inject.Qualifiers;
import me.yushust.inject.key.Key;
import me.yushust.inject.key.TypeReference;
import me.yushust.inject.key.CompositeTypeReflector;
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

  public InjectableConstructor getConstructor(TypeReference<?> type) {

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
      return InjectableConstructor.DUMMY;
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

  public List<InjectableMember> getMembers(TypeReference<?> type) {

    // Initially only contains the fields,
    // then all the methods are added to this list
    List<InjectableMember> members = new ArrayList<InjectableMember>();
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
        TypeReference<?> fieldType = TypeReference.of(
            CompositeTypeReflector.resolveContextually(
                type, field.getGenericType()
            )
        );
        OptionalDefinedKey<?> key = keyOf(fieldType, field.getAnnotations());
        members.add(new InjectableField(type, key, field));
      }
    }

    members.addAll(getMethods(type));
    return members;
  }

  public List<InjectableMethod> getMethods(TypeReference<?> type) {

    List<InjectableMethod> methods = new ArrayList<InjectableMethod>();
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
        if (!method.isAnnotationPresent(Inject.class)) {
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
        new ArrayList<OptionalDefinedKey<?>>();
    for (int i = 0; i < parameterTypes.length; i++) {
      Type parameter = parameterTypes[i];
      Annotation[] annotations = parameterAnnotations[i];
      TypeReference<?> parameterType = TypeReference.of(
          CompositeTypeReflector.resolveContextually(
              declaringType, parameter
          )
      );
      keys.add(keyOf(parameterType, annotations));
    }
    return keys;
  }

  private OptionalDefinedKey<?> keyOf(TypeReference<?> type, Annotation[] annotations) {
    boolean optional = false;
    for (Annotation annotation : annotations) {
      Class<? extends Annotation> annotationType = annotation.annotationType();
      if (!optional) {
        String simpleName = annotationType.getSimpleName();
        // Please use "Nullable" instead of "nullable"
        if (simpleName.equalsIgnoreCase("Nullable")) {
          optional = true;
        }
      }
    }
    @SuppressWarnings({"rawtypes"})
    Key key = Key.of(type, Qualifiers.getQualifiers(qualifierFactory, annotations));
    @SuppressWarnings("unchecked")
    OptionalDefinedKey<?> optionalDefinedKey =
        new OptionalDefinedKey<Object>(key, optional);
    return optionalDefinedKey;
  }

}
