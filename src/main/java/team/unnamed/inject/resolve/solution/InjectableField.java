/*
 * This file is part of inject, licensed under the MIT license
 *
 * Copyright (c) 2021-2023 Unnamed Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
