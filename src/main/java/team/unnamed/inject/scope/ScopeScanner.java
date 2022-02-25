package team.unnamed.inject.scope;

import team.unnamed.inject.util.Validate;

import javax.inject.Singleton;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.HashMap;
import java.util.Map;

/**
 * Class responsible of scanning
 * scope annotations from {@link AnnotatedElement}
 */
public final class ScopeScanner {

    private final Map<Class<? extends Annotation>, Scope> scopes
            = new HashMap<>();

    ScopeScanner() {
        scopes.put(Singleton.class, Scopes.SINGLETON);
    }

    /**
     * Binds the given {@code annotationType} to
     * the specified {@code scope} instance.
     *
     * <p>Note that this method doesn't require
     * the {@code annotationType} to be annotated
     * with {@link javax.inject.Scope}</p>
     */
    public void bind(Class<? extends Annotation> annotationType, Scope scope) {
        Validate.notNull(annotationType, "annotationType");
        Validate.notNull(scope, "scope");
        scopes.put(annotationType, scope);
    }

    /**
     * Scans the given {@code element} annotations searching
     * for annotations present in the internal {@code scopes}
     * map
     *
     * @return The found scope, {@link Scopes#NONE} if no scopes
     * found
     */
    public Scope scan(AnnotatedElement element) {
        Annotation[] annotations = element.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            Class<? extends Annotation> annotationType = annotation.annotationType();
            Scope scope = scopes.get(annotationType);
            if (scope != null) {
                return scope;
            }
        }
        return Scopes.NONE;
    }

}
