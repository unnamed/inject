package me.yushust.inject.key;

import me.yushust.inject.util.Validate;

import java.util.Objects;

/**
 * An extension for {@link Key} (using composition over inheritance)
 * that adds two boolean states representing the requirement of the
 * injection of this key and if this key will be assisted or not.
 */
public final class InjectedKey<T> {

	private final Key<T> key;
	private final boolean optional;
	private final boolean assisted;

	public InjectedKey(Key<T> key, boolean optional, boolean assisted) {
		this.key = Validate.notNull(key, "key");
		this.optional = optional;
		this.assisted = assisted;
	}

	public Key<T> getKey() {
		return key;
	}

	public boolean isOptional() {
		return optional;
	}

	public boolean isAssisted() {
		return assisted;
	}

	@Override
	public String toString() {
		return (optional ? "(optional) " : "(required) ")
				+ (assisted ? "(assisted) " : "")
				+ key.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		InjectedKey<?> that = (InjectedKey<?>) o;
		return optional == that.optional
				&& assisted == that.assisted
				&& key.equals(that.key);
	}

	@Override
	public int hashCode() {
		return Objects.hash(optional, assisted, key);
	}
}
