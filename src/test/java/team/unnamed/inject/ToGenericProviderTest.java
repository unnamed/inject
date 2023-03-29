package team.unnamed.inject;

import team.unnamed.inject.key.Key;
import team.unnamed.inject.provision.std.generic.GenericProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ToGenericProviderTest {

    @Inject
    private Foo<String> stringFoo;
    @Inject
    private Foo<String> stringFoo2;

    @Inject
    private Foo<Integer> intFoo;
    @Inject
    private Foo<Integer> intFoo2;

    @Inject
    private Foo<Double> doubleFoo;
    @Inject
    private Foo<Double> doubleFoo2;

    @Test
    public void test() {

        Injector injector = Injector.create(binder ->
                binder.bind(Foo.class).toGenericProvider(new FooGenericProvider()).singleton());

        injector.injectMembers(this);

        Assertions.assertSame(stringFoo, stringFoo2);
        Assertions.assertSame(intFoo, intFoo2);
        Assertions.assertSame(doubleFoo, doubleFoo2);
    }

    public interface Foo<T> {

        T give();

    }

    public static class FooGenericProvider implements GenericProvider<Foo<?>> {

        @Override
        public Foo<?> get(Key<?> match) {

            Type parameterType = ((ParameterizedType) match.getType().getType())
                    .getActualTypeArguments()[0];
            Foo<?> foo = null;

            if (parameterType == String.class) {
                foo = new StringFoo();
            } else if (parameterType == Integer.class) {
                foo = new IntegerFoo();
            } else if (parameterType == Double.class) {
                foo = new DoubleFoo();
            }
            return foo;
        }

    }

    private static class DoubleFoo implements Foo<Double> {

        @Override
        public Double give() {
            return (double) System.nanoTime();
        }

    }

    private static class IntegerFoo implements Foo<Integer> {

        @Override
        public Integer give() {
            return (int) System.nanoTime();
        }

    }

    private static class StringFoo implements Foo<String> {

        @Override
        public String give() {
            return System.nanoTime() + "";
        }

    }

}
