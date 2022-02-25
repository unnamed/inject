package team.unnamed.inject.provision.std.generic.impl;

import team.unnamed.inject.key.Key;
import team.unnamed.inject.key.TypeReference;
import team.unnamed.inject.provision.std.generic.GenericProvider;
import team.unnamed.inject.util.Validate;

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
