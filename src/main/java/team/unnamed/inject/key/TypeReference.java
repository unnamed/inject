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
package team.unnamed.inject.key;

import team.unnamed.inject.key.Types.AbstractTypeWrapper;
import team.unnamed.inject.key.Types.CompositeType;
import team.unnamed.inject.util.Validate;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

public class TypeReference<T> extends AbstractTypeWrapper implements CompositeType {

    private final Class<T> rawType;
    private final Type type;

    @SuppressWarnings("unchecked")
    protected TypeReference() {

        Type superClass = getClass().getGenericSuperclass();

        Validate.state(superClass instanceof ParameterizedType,
                "Invalid TypeReference creation.");

        ParameterizedType parameterized = (ParameterizedType) superClass;

        this.type = Types.compose(parameterized.getActualTypeArguments()[0]);
        this.rawType = (Class<T>) Types.getRawType(type);
        super.components.add(this.type);
    }

    @SuppressWarnings("unchecked")
    TypeReference(Type type) {
        Validate.notNull(type);
        this.type = Types.compose(type);
        this.rawType = (Class<T>) Types.getRawType(this.type);
        super.components.add(this.type);
    }

    @SuppressWarnings("unchecked")
    TypeReference(Type type, Class<? super T> rawType) {
        Validate.notNull(type, "type");
        Validate.notNull(rawType, "rawType");
        this.type = Types.compose(type);
        this.rawType = (Class<T>) Types.getRawType(rawType); // convert primitives to wrapper types
        super.components.add(this.type);
    }

    public static <T> TypeReference<T> of(Type type) {
        return new TypeReference<>(type);
    }

    public static <T> TypeReference<T> of(Class<?> rawType, Type... typeArguments) {
        Validate.notNull(rawType);
        return of(Types.parameterizedTypeOf(null, rawType, typeArguments));
    }

    public static <K, V> TypeReference<Map<K, V>> mapTypeOf(TypeReference<K> key, TypeReference<V> value) {
        return of(Map.class, key.getType(), value.getType());
    }

    /**
     * Determines if the {@link TypeReference} represented by this is a raw-type
     */
    public final boolean isPureRawType() {
        return type == rawType;
    }

    public final TypeReference<?> getFieldType(Field field) {
        Validate.notNull(field, "field");
        Validate.argument(
                field.getDeclaringClass().isAssignableFrom(rawType),
                "Field '%s' isn't present in any super-type of '%s'",
                field.getName(),
                rawType
        );
        Type resolvedType = CompositeTypeReflector.resolveContextually(
                this, field.getGenericType()
        );
        @SuppressWarnings({"rawtypes", "unchecked"})
        TypeReference fieldType = new TypeReference(resolvedType, field.getType());
        return fieldType;
    }

    /**
     * Unsafe method, type must be the type of a member of this type,
     * else, it throws a {@link IllegalStateException}
     */
    public final TypeReference<?> resolve(Type type) {
        Validate.notNull(type, "type");
        type = CompositeTypeReflector.resolveContextually(this, type);
        return new TypeReference<>(type);
    }

    public final Class<T> getRawType() {
        return rawType;
    }

    public final Type getType() {
        return type;
    }

    /**
     * Removes the reference for the upper class. For
     * anonymous classes. If you store a {@link TypeReference}
     * in cache, you should execute this method before.
     *
     * @return The type reference.
     */
    public final TypeReference<T> canonicalize() {
        // This object isn't an instance of
        // an anonymous class
        if (getClass() == TypeReference.class) {
            return this;
        } else {
            return new TypeReference<>(type, rawType);
        }
    }

    @Override
    public final int hashCode() {
        return type.hashCode();
    }

    @Override
    public final boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof TypeReference<?>)) return false;
        TypeReference<?> other = (TypeReference<?>) o;
        return type.equals(other.type);
    }

    @Override
    public final String toString() {
        return Types.getTypeName(type);
    }

    @Override
    protected final Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    protected final void finalize() throws Throwable {
        super.finalize();
    }

}
