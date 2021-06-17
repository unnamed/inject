package me.yushust.inject.impl;

import me.yushust.inject.Binder;
import me.yushust.inject.key.Key;
import me.yushust.inject.util.Validate;

import java.lang.annotation.Annotation;

/**
 * Removes the responsibility to the implementer class
 * of implement this methods. This interface behaves
 * like an abstract class (it's not an abstract class
 * because sometimes we need multiple "super-classes")
 */
public interface KeyBuilder<R, T> extends Binder.Qualified<R> {

	Key<T> key();

	void setKey(Key<T> key);

	@Override
	default R markedWith(Class<? extends Annotation> qualifierType) {
		Validate.notNull(qualifierType, "qualifierType");
		setKey(key().withQualifier(qualifierType));
		return getReturnValue();
	}

	@Override
	default R qualified(Annotation annotation) {
		Validate.notNull(annotation, "annotation");
		setKey(key().withQualifier(annotation));
		return getReturnValue();
	}

	@Override
	default R named(String name) {
		Validate.notNull(name, "name");
		return qualified(Annotations.createNamed(name));
	}

	R getReturnValue();

}
