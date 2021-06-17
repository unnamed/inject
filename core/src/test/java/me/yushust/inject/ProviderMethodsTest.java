package me.yushust.inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.UUID;

public class ProviderMethodsTest {

	@Test
	public void test() {
		Injector injector = Injector.create(new MyModule());
		Foo foo = injector.getInstance(Foo.class);
		Assertions.assertEquals("trew", foo.trew);
		Assertions.assertEquals("hello", foo.hello);
		Assertions.assertEquals("yusshu", foo.author);
		Assertions.assertSame(foo.id1, foo.id2);
		Assertions.assertNotNull(foo.component1);
		Assertions.assertNotNull(foo.component2);
		Assertions.assertNotNull(foo.component12);
		Assertions.assertNotNull(foo.component22);
	}

	public interface Component1 {
	}

	public interface Component2 {
	}

	public static class Component1Impl implements Component1 {
	}

	public static class Component1Impl2 implements Component1 {
	}

	public static class Component2Impl implements Component2 {
	}

	public static class Component2Impl2 implements Component2 {
	}

	public static class MyModule extends AbstractModule {

		@Override
		protected void configure() {
			bind(Component1.class).to(Component1Impl.class).singleton();
			bind(Component1.class).named("2").to(Component1Impl2.class).singleton();

			bind(Component2.class).to(Component2Impl.class).singleton();
			bind(Component2.class).named("2").to(Component2Impl2.class).singleton();
		}

		@Provides
		public String provideName(Component1 component1, Component2 component2) {
			Assertions.assertNotNull(component1);
			Assertions.assertNotNull(component2);
			return "trew";
		}

		@Provides
		@Named("hello")
		public String provideHello(@Named("2") Component1 component1, @Named("2") Component2 component2) {
			Assertions.assertNotNull(component1);
			Assertions.assertNotNull(component2);
			return "hello";
		}

		@Provides
		@Named("author")
		public String provideAuthor(String name) {
			Assertions.assertEquals("trew", name);
			return "yusshu";
		}

		@Provides
		@Singleton
		public UUID provideId(@Named("author") String author) {
			Assertions.assertEquals("yusshu", author);
			return UUID.randomUUID();
		}
	}

	public static class Foo {
		@Inject private String trew;
		@Inject
		@Named("hello")
		private String hello;
		@Inject private UUID id1;
		@Inject private UUID id2;
		@Inject
		@Named("author")
		private String author;
		@Inject private Component1 component1;
		@Inject private Component2 component2;
		@Inject
		@Named("2")
		private Component1 component12;
		@Inject
		@Named("2")
		private Component2 component22;
	}

}
