package me.yushust.inject.resolve;

import me.yushust.inject.error.ErrorAttachable;
import me.yushust.inject.key.TypeReference;
import me.yushust.inject.resolve.solution.InjectableConstructor;

import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;

public final class ConstructorResolver {

	/** Sentinel value for indicate that a constructor was not resolved yet */
	static final Object CONSTRUCTOR_NOT_DEFINED = new Object();

	ConstructorResolver() {
	}

	/**
	 * Cached alternative method for {@link ConstructorResolver#resolve},
	 * this method always uses the {@link Inject} annotation to resolve the constructors
	 */
	public InjectableConstructor get(
			ErrorAttachable errors,
			TypeReference<?> type
	) {
		Solution solution = ComponentResolver.SOLUTIONS.get(type);
		// null constructor is valid and indicates that the constructor was
		// already resolved, the sentinel value indicates that the constructor
		// was never resolved!
		if (solution == null || solution.constructor == CONSTRUCTOR_NOT_DEFINED) {
			if (solution == null) {
				solution = new Solution();
				ComponentResolver.SOLUTIONS.put(type, solution);
			}
			solution.constructor = resolve(errors, type, Inject.class);
		}
		// so it's null or an instance of injectable constructor
		return (InjectableConstructor) solution.constructor;
	}

	/**
	 * @return Returns the first injectable constructor
	 * found for the specified {@code type}.
	 *
	 * <p>If no constructor annotated with the given {@code annotation}
	 * is found, the default/empty constructor is used (constructor
	 * without parameters)</p>
	 */
	public InjectableConstructor resolve(
			ErrorAttachable errors,
			TypeReference<?> type,
			Class<? extends Annotation> annotation
	) {

		Class<?> rawType = type.getRawType();

		Constructor<?> injectableConstructor = null;
		for (Constructor<?> constructor : rawType.getDeclaredConstructors()) {
			if (constructor.isAnnotationPresent(annotation)) {
				injectableConstructor = constructor;
				break;
			}
		}

		if (injectableConstructor == null) {
			try {
				injectableConstructor = rawType.getDeclaredConstructor();
			} catch (NoSuchMethodException ignored) {
			}
		}

		if (injectableConstructor == null) {
			errors.attach("No constructor found for type '" + type + "'");
			return null;
		}

		return new InjectableConstructor(
				ComponentResolver.keys().keysOf(
						type,
						injectableConstructor.getParameters()
				),
				injectableConstructor
		);
	}

}
