package team.unnamed.inject.resolve.solution;

import team.unnamed.inject.impl.InjectorImpl;
import team.unnamed.inject.impl.ProvisionStack;
import team.unnamed.inject.key.InjectedKey;
import team.unnamed.inject.key.TypeReference;
import team.unnamed.inject.util.ElementFormatter;
import team.unnamed.inject.util.Validate;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Objects;

/**
 * Represents a Field annotated with {@link team.unnamed.inject.Inject}
 * and that already has resolved a key, with its requirement level
 * defined too.
 */
public class InjectableField implements InjectableMember {

    private final TypeReference<?> declaringType;
    private final InjectedKey<?> key;
    private final Field field;

    public InjectableField(
            TypeReference<?> declaringType,
            InjectedKey<?> key,
            Field field
    ) {
        this.declaringType = Validate.notNull(declaringType, "declaringType");
        this.key = Validate.notNull(key, "key");
        this.field = Validate.notNull(field, "field");

        Validate.doesntRequiresContext(key.getKey());
        this.field.setAccessible(true); // bro...
    }

    @Override
    public TypeReference<?> getDeclaringType() {
        return declaringType;
    }

    @Override
    public Field getMember() {
        return field;
    }

    @Override
    public Object inject(InjectorImpl injector, ProvisionStack stack, Object target) {

        if (target == null ^ Modifier.isStatic(field.getModifiers())) {
            return null;
        }

        Object value = injector.getValue(key, stack);

        if (value == InjectorImpl.ABSENT_INSTANCE) {
            stack.attach(
                    "Cannot inject '" + field.getName() + "' field."
                            + "\n\tAt:" + declaringType
                            + "\n\tReason: Cannot get value for required key"
                            + " \n\tRequired Key: " + key.getKey()
            );
            return null;
        }

        try {
            field.set(target, value);
        } catch (IllegalAccessException e) {
            stack.attach(
                    "Cannot inject field "
                            + ElementFormatter.formatField(field, key),
                    e
            );
        }

        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InjectableField that = (InjectableField) o;
        return declaringType.equals(that.declaringType) &&
                key.equals(that.key) &&
                field.equals(that.field);
    }

    @Override
    public int hashCode() {
        return Objects.hash(declaringType, key, field);
    }

}
