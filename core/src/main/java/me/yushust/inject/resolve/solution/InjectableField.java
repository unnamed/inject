package me.yushust.inject.resolve.solution;

import me.yushust.inject.util.ElementFormatter;
import me.yushust.inject.error.ErrorAttachable;
import me.yushust.inject.key.InjectedKey;
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
  private final InjectedKey<?> key;
  private final Field field;

  public InjectableField(TypeReference<?> declaringType,
                         InjectedKey<?> key,
                         Field field) {
    this.declaringType = Validate.notNull(declaringType, "declaringType");
    this.key = Validate.notNull(key, "key");
    this.field = Validate.notNull(field, "field");

    Validate.doesntRequiresContext(key.getKey());
    this.field.setAccessible(true); // bro...
  }

  public TypeReference<?> getDeclaringType() {
    return declaringType;
  }

  public Field getMember() {
    return field;
  }

  public List<InjectedKey<?>> getKeys() {
    return Collections.singletonList(key);
  }

  /**
   * Injects the value ({@code values} must be an array of 1 value) to
   * the already specified field in the provided {@code target}
   *
   * @throws IllegalArgumentException If the target is null and the field
   *                                  isn't static, or if the target isn't present in the type or its supertypes,
   *                                  or if the {@code values} length isn't 1
   */
  @Override
  public Object inject(ErrorAttachable errors, Object target, Object[] values) {

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
      errors.attach("Cannot set value in field " + ElementFormatter.formatField(field, key), e);
    }

    return null;
  }

  @Override
  public String toString() {
    return "Field '" + field.getName() + "' of type '" + key.getKey() + "'";
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
    int result = 1;
    result = 31 * result + declaringType.hashCode();
    result = 31 * result + key.hashCode();
    result = 31 * result + field.hashCode();
    return result;
  }
}
