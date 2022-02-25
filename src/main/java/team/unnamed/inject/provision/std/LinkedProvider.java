package team.unnamed.inject.provision.std;

import team.unnamed.inject.impl.InjectorImpl;
import team.unnamed.inject.impl.ProvisionStack;
import team.unnamed.inject.key.Key;
import team.unnamed.inject.provision.StdProvider;

/**
 * Represents a link of a key to another key (can be the same).
 * The provider gets an instance of the target type instead
 * of getting an instance of the original key.
 *
 * <p>For this example</p>
 * <pre>
 * public interface Foo {
 *   // ...
 * }
 *
 * public class Bar implements Foo {
 *   // ...
 * }
 * </pre>
 * <p>The link will be</p>
 * {@code bind(Foo.class).to(Bar.class);}
 */
public class LinkedProvider<T>
        extends StdProvider<T> {

    private final Key<T> key;
    private final Key<? extends T> target;
    private final boolean autoBound;

    private InjectorImpl injector;

    public LinkedProvider(Key<T> key, Key<? extends T> target) {
        this.key = key;
        this.target = target;
        this.autoBound = key.equals(target);
    }

    @Override
    public void inject(ProvisionStack stack, InjectorImpl injector) {
        // Sets the injector, used to get an instance of the target type
        this.injector = injector;
        this.injected = true;
    }

    @Override
    public T get() {
        // the injector should not use the explicit
        // bindings if the key is bound to the same
        // key. Else, it will call this get() method
        // again, and again, ending in a StackOverflowError
        return injector.getInstance(target, !autoBound);
    }

    /**
     * Determines if the linked provider is linked to the same key
     */
    public boolean isAutoBound() {
        return autoBound;
    }

    /**
     * @return The target linked key
     */
    public Key<? extends T> getTarget() {
        return target;
    }

    @Override
    public String toString() {
        if (key.equals(target)) {
            return "same key";
        } else {
            return "linked key '" + target + "'";
        }
    }

}
