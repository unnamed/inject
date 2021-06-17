package me.yushust.inject;

import me.yushust.inject.key.TypeReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class GenericInjectionTest2 {

	@Test
	public void test() {

		Injector injector = Injector.create(binder ->
				binder.install(new DynamicModule<>(new TypeReference<String>() {
				})));

		TypeReference<List<String>> expected = new TypeReference<List<String>>() {
		};
		TypeReference<List<String>> type = injector.getInstance(new TypeReference<TypeReference<List<String>>>() {
		});

		Assertions.assertEquals(expected, type);

		Foo<String> val = injector.getInstance(TypeReference.of(Foo.class, String.class));
		Assertions.assertNotNull(val);
	}

	public interface Foo<T> {
	}

	public static class DynamicModule<T> implements Module {

		private final TypeReference<T> bound;

		public DynamicModule(TypeReference<T> bound) {
			this.bound = bound;
		}

		@Override
		public void configure(Binder binder) {

			binder.bind(
					// Okay this is bad but we support this
					TypeReference.of(Foo.class, bound)
			).to(
					TypeReference.of(FooImpl.class, bound)
			);
		}
	}

	public static class FooImpl<T> implements Foo<T> {
	}

}
