package team.unnamed.inject.scope;

import javax.inject.Provider;

/**
 * Collection of built-in scopes
 */
public final class Scopes {

    public static final Scope SINGLETON
            = new LazySingletonScope();

    public static final Scope NONE
            = EmptyScope.INSTANCE;

    private static final ScopeScanner SCANNER
            = new ScopeScanner();

    private Scopes() {
    }

    /**
     * Returns the scope scanner instance
     */
    public static ScopeScanner getScanner() {
        return SCANNER;
    }

    /**
     * Represents an scope that always returns the
     * same unscoped provider. The implementation is
     * an enum to let the JVM make sure only one
     * instance exists.
     */
    private enum EmptyScope implements Scope {
        INSTANCE;

        @Override
        public <T> Provider<T> scope(Provider<T> unscoped) {
            return unscoped;
        }

        @Override
        public String toString() {
            return "EmptỳScope";
        }
    }

}
