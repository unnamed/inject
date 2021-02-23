package me.yushust.inject.internal;

import me.yushust.inject.ProvidedBy;
import me.yushust.inject.Targetted;
import me.yushust.inject.key.Key;
import me.yushust.inject.key.TypeReference;
import me.yushust.inject.provision.Providers;
import me.yushust.inject.provision.StdProvider;
import me.yushust.inject.scope.Scopes;

import javax.inject.Provider;
import javax.inject.Singleton;
import java.lang.reflect.Modifier;

/**
 * Scans a type looking for scope annotations
 * and binding annotations like {@link Singleton},
 * {@link Targetted}, {@link ProvidedBy}.
 */
final class AnnotationScanner {

  private AnnotationScanner() {
  }

  /** Scans the specified type if it's not bound. It binds the type its annotations*/
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

  /** Scopes the specified type using its annotations. */
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

    if (rawType.isAnnotationPresent(Singleton.class)) {
      binder.$unsafeBind(key, Providers.scope(key, provider, Scopes.SINGLETON));
    }
  }

}
