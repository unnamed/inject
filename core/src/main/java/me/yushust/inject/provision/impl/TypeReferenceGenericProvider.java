package me.yushust.inject.provision.impl;

import me.yushust.inject.GenericProvider;
import me.yushust.inject.error.InjectionException;
import me.yushust.inject.key.TypeReference;

public class TypeReferenceGenericProvider implements GenericProvider<TypeReference<?>> {

  @Override
  public TypeReference<?> get(
      Class<?> rawType,
      TypeReference<?>[] parameters
  ) {
    if (parameters.length != 1) {
      throw new InjectionException("Cannot inject a non-specific TypeReference " + rawType);
    } else {
      return parameters[0];
    }
  }

}
