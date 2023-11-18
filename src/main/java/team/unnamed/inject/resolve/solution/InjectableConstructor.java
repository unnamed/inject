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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;

/**
 * Represents an injectable constructor, a constructor
 * annotated with {@link team.unnamed.inject.Inject} or a
 * constructor with no parameters.
 */
public class InjectableConstructor implements InjectableMember {

    private final List<InjectedKey<?>> keys;

    private final TypeReference<?> declaringType;
    private final Constructor<?> constructor;

    public InjectableConstructor(
            List<InjectedKey<?>> keys,
            Constructor<?> constructor
    ) {
        this.keys = Collections.unmodifiableList(keys);
        this.constructor = constructor;

        for (InjectedKey<?> key : keys) {
            Validate.doesntRequiresContext(key.getKey());
        }
        if (constructor != null) {
            this.constructor.setAccessible(true);
            this.declaringType = TypeReference.of(constructor.getDeclaringClass());
        } else {
            this.declaringType = null;
        }
    }

    @Override
    public TypeReference<?> getDeclaringType() {
        return declaringType;
    }

    @Override
    public Constructor<?> getMember() {
        return constructor;
    }

    public List<InjectedKey<?>> getKeys() {
        return keys;
    }

    @Override
    public Object inject(InjectorImpl injector, ProvisionStack stack, Object target) {

        Object[] values = new Object[keys.size()];

        for (int i = 0; i < keys.size(); i++) {
            InjectedKey<?> key = keys.get(i);
            Object value = injector.getValue(key, stack);

            if (value == InjectorImpl.ABSENT_INSTANCE) {
                stack.attach(
                        "Cannot instantiate class"
                                + "\n\tClass: " + constructor.getName()
                                + "\n\tReason: Cannot get value for required parameter (index " + i + ")"
                                + " \n\tRequired Key: " + key.getKey()
                );
                return null;
            } else {
                values[i] = value;
            }
        }

        try {
            return constructor.newInstance(values);
        } catch (
                InstantiationException
                        | IllegalAccessException
                        | InvocationTargetException e
        ) {
            stack.attach(
                    "Errors while constructing "
                            + ElementFormatter.formatConstructor(constructor, keys),
                    e
            );
        }
        return null;
    }

}
