package me.yushust.inject.internal;

import me.yushust.inject.error.ErrorAttachable;
import me.yushust.inject.key.Key;
import me.yushust.inject.key.TypeReference;
import me.yushust.inject.resolve.InjectableConstructor;
import me.yushust.inject.resolve.InjectableMember;
import me.yushust.inject.resolve.MembersResolver;
import me.yushust.inject.resolve.OptionalDefinedKey;

import javax.inject.Inject;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class InstanceFactoryCreator {

  private final MembersResolver membersResolver;

  InstanceFactoryCreator(MembersResolver membersResolver) {
    this.membersResolver = membersResolver;
  }

  <T> T createFactory(ErrorAttachable errors, Class<T> clazz) {

    if ((!clazz.isInterface()
        && !Modifier.isAbstract(clazz.getModifiers()))
        || clazz.getMethods().length != 1) {
      errors.attach("The class '" + clazz.getName() + "' isn't a valid factory. Factories" +
          "are interfaces or abstract classes with one method");
      return null;
    }

    Method creatorMethod = clazz.getMethods()[0];
    Class<?> rawReturnType = creatorMethod.getReturnType();

    if (rawReturnType == void.class) {
      errors.attach("The class '" + clazz.getName() + " isn't a valid factory. " +
          "It does return nothing!");
      return null;
    }

    Parameter[] parameters = creatorMethod.getParameters();
    TypeReference<?> createdType = TypeReference.of(creatorMethod.getGenericReturnType());

    InjectableConstructor constructor = membersResolver.getConstructor(errors, createdType);

    if (constructor == null) {
      return null;
    }

    Set<Key<?>> required = new HashSet<>();
    Set<Key<?>> optional = new HashSet<>();

    countAssisted(membersResolver.getFields(createdType), required, optional);
    countAssisted(membersResolver.getMethods(createdType, Inject.class), required, optional);

    int min = required.size();
    int max = required.size() + optional.size();

    if (parameters.length < min || parameters.length > max) {
      errors.attach("Assisted members mismatch! Class " + createdType
          + " requires from " + min + " to " + max + " assisted injections, but factory gives " + parameters.length + " injected members!");
      return null;
    }


    return null;
  }

  private void countAssisted(
      List<? extends InjectableMember> members,
      Set<Key<?>> required,
      Set<Key<?>> optionalExtra
  ) {
    for (InjectableMember member : members) {
      for (OptionalDefinedKey<?> key : member.getKeys()) {
        if (key.isAssisted()) {
          if (key.isOptional()) {
            optionalExtra.add(key.getKey());
          } else {
            required.add(key.getKey());
          }
        }
      }
    }
  }

  void check(ProvisionStack stack, TypeReference<?> type) {

  }

}
