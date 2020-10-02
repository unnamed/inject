package me.yushust.inject.resolve;

import me.yushust.inject.error.ErrorAttachable;
import me.yushust.inject.key.TypeReference;
import me.yushust.inject.util.Validate;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;

/**
 * Represents a Field annotated with {@link javax.inject.Inject}
 * and that already has resolved a key, with its requirement level
 * defined too.
 */
public class InjectableField implements InjectableMember {

  private final TypeReference<?> declaringType;
  private final OptionalDefinedKey<?> key;
  private final Field field;

  public InjectableField(TypeReference<?> declaringType,
                         OptionalDefinedKey<?> key,
                         Field field) {
    this.declaringType = Validate.notNull(declaringType, "declaringType");
    this.key = Validate.notNull(key, "key");
    this.field = Validate.notNull(field, "field");
    this.field.setAccessible(true); // bro...
  }

  public TypeReference<?> getDeclaringType() {
    return declaringType;
  }

  public Field getMember() {
    return field;
  }

  public List<OptionalDefinedKey<?>> getKeys() {
    return Collections.<OptionalDefinedKey<?>>singletonList(key);
  }

  /**
   * Injects the value ({@code values} must be an array of 1 value) to
   * the already specified field in the provided {@code target}
   *
   * @throws IllegalArgumentException If the target is null and the field
   *                                  isn't static, or if the target isn't present in the type or its supertypes,
   *                                  or if the {@code values} length isn't 1
   */
  public void inject(ErrorAttachable errors, Object target, Object[] values) {

    Validate.argument(
        target != null
            || Modifier.isStatic(field.getModifiers()),
        "Target instance is null and the field isn't static!"
    );
    Validate.argument(
        target == null
            || declaringType.getRawType().isAssignableFrom(target.getClass()),
        "Field isn't present in the target class"
    );
    Validate.argument(values.length == 1, "Cannot inject multiple values to a field!");

    try {
      field.set(target, values[0]);
    } catch (IllegalAccessException e) {
      errors.attach(e);
    }
  }
}
