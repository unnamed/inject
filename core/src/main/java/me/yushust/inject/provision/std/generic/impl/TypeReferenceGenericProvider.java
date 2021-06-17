package me.yushust.inject.provision.std.generic.impl;

import me.yushust.inject.key.Key;
import me.yushust.inject.key.TypeReference;
import me.yushust.inject.provision.std.generic.GenericProvider;
import me.yushust.inject.util.Validate;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class TypeReferenceGenericProvider
		implements GenericProvider<TypeReference<?>> {

	@Override
	public TypeReference<?> get(Key<?> match) {
		TypeReference<?> typeMirror = match.getType();
		Type type = typeMirror.getType();

		Validate.state(
				type instanceof ParameterizedType,
				"Unsupported type '" + type + "'. The type must be "
						+ "a parameterized type."
		);

		return TypeReference.of(
				((ParameterizedType) type)
						.getActualTypeArguments()[0]
		);
	}

}
