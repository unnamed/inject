package me.yushust.inject.provision.std;

import me.yushust.inject.GenericProvider;
import me.yushust.inject.internal.BinderImpl;
import me.yushust.inject.key.Key;
import me.yushust.inject.key.TypeReference;
import me.yushust.inject.provision.StdProvider;
import me.yushust.inject.provision.ioc.BindListener;
import me.yushust.inject.util.Validate;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ToGenericProvider<T>
    extends StdProvider<T>
    implements BindListener {

  private final GenericProvider<T> provider;

  public ToGenericProvider(GenericProvider<T> provider) {
    this.provider = Validate.notNull(provider, "provider");
  }

  @Override
  public boolean onBind(BinderImpl binder, Key<?> key) {

    boolean isRawType = key.getType().getType() == key.getType().getRawType();

    if (!isRawType) {
      binder.attach("You must bound the raw-type to a GenericProvider, " +
          "not a parameterized type! (key: " + key + ", genericProvider: " + provider + ")");
    }

    return isRawType;
  }

  @Override
  public T get() {
    throw new IllegalStateException("Key was bound to a generic provider," +
        " it cannot complete a raw-type!\n\tProvider: " + provider);
  }

  /** Special injector case for keys bound to generic providers */
  public T get(Key<?> bound) {
    if (bound.getType().getType() == bound.getType().getRawType()) {
      get(); // throws an exception
      return null; // this never passes
    }

    Type type = bound.getType().getType();
    if (!(type instanceof ParameterizedType)) {
      throw new IllegalStateException("Matched key isn't a ParameterizedType!\n\tKey: "
          + bound + "\n\tProvider: " + provider);
    }

    ParameterizedType parameterizedType = (ParameterizedType) type;
    Type[] typeParameters = parameterizedType.getActualTypeArguments();
    TypeReference<?>[] handleableTypeParameters = new TypeReference[typeParameters.length];

    for (int i = 0; i < typeParameters.length; i++) {
      handleableTypeParameters[i] = TypeReference.of(typeParameters[i]);
    }

    return provider.get(bound.getType().getRawType(), handleableTypeParameters);
  }
}
