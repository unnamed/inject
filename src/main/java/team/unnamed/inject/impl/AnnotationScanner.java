package team.unnamed.inject.impl;

import team.unnamed.inject.ProvidedBy;
import team.unnamed.inject.Provider;
import team.unnamed.inject.Singleton;
import team.unnamed.inject.Targetted;
import team.unnamed.inject.key.Key;
import team.unnamed.inject.key.TypeReference;
import team.unnamed.inject.provision.Providers;
import team.unnamed.inject.provision.StdProvider;
import team.unnamed.inject.scope.Scope;
import team.unnamed.inject.scope.Scopes;

import java.lang.reflect.Modifier;

/**
 * Scans a type looking for scope annotations
 * and binding annotations like {@link Singleton},
 * {@link Targetted}, {@link ProvidedBy}.
 */
final class AnnotationScanner {

    private AnnotationScanner() {
    }

    /**
     * Scans the specified type if it's not bound. It binds the type its annotations
     */
    static <T> void bind(TypeReference<T> keyType, BinderImpl binder) {

        Key<T> key = Key.of(keyType);
        StdProvider<? extends T> provider = binder.getProvider(key);

        // it's already explicit-bound
        if (provider != null) {
            return;
        }

        Class<? super T> rawType = keyType.getRawType();

        Targetted target = rawType.getAnnotation(Targetted.class);
        ProvidedBy providedBy = rawType.getAnnotation(ProvidedBy.class);

        if (target != null) {
            Key<? extends T> linkedKey = Key.of(TypeReference.of(target.value()));
            binder.$unsafeBind(key, Providers.link(key, linkedKey));
        } else if (providedBy != null) {
            TypeReference<? extends Provider<? extends T>> linkedProvider =
                    TypeReference.of(providedBy.value());
            binder.$unsafeBind(key, Providers.providerTypeProvider(linkedProvider));
        }
    }

    /**
     * Scopes the specified type using its annotations.
     */
    static <T> void scope(TypeReference<T> keyType, BinderImpl binder) {

        Key<T> key = Key.of(keyType);
        StdProvider<? extends T> provider = binder.getProvider(key);

        Class<? super T> rawType = keyType.getRawType();

        // so it can be linked to itself
        if (provider == null && !rawType.isInterface()
                && !Modifier.isAbstract(rawType.getModifiers())) {
            // link to self
            provider = Providers.normalize(Providers.link(key, key));
        }

        // if there's no a provider it cannot
        // be scoped!
        if (provider == null) {
            return;
        }

        Scope scope = Scopes.getScanner().scan(rawType);
        if (scope != Scopes.NONE) {
            binder.$unsafeBind(key, provider.withScope(key, scope));
        }
    }

}
