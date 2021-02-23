package me.yushust.inject.property;

import me.yushust.inject.impl.ProvisionHandle;
import me.yushust.inject.impl.ProvisionStack;
import me.yushust.inject.key.Key;
import me.yushust.inject.key.Qualifier;
import me.yushust.inject.key.TypeReference;

import javax.inject.Provider;

public class PropertyRequestHandle {

  private static final Key<PropertyHolder> PROPERTY_HOLDER_KEY =
      Key.of(PropertyHolder.class);

  public static <T> T getProperty(
      String path,
      TypeReference<?> type,
      ProvisionHandle provisionHandle,
      ProvisionStack stack
  ) {
    Class<?> rawType = type.getRawType();
    // We know the type and there's no necessity to invoke
    // getInstance(...) again, because it's an interface
    // (it cannot be instantiated), the unique way to get
    // an instance of PropertyHolder is with an explicit binding
    Provider<PropertyHolder> provider =
        provisionHandle.getProviderAndInject(stack, PROPERTY_HOLDER_KEY);

    PropertyHolder propertyHolder;

    // An injection request for properties has been made and there's
    // no a properties source!
    if (provider == null ||
        (propertyHolder = provider.get()) == null) {
      stack.attach("There's no a PropertyHolder bound and a" +
          " member annotated with @Property exists! " + type);
      return null;
    }

    Object propertyValue = PropertyRequestHandle.tryConvert(rawType, propertyHolder.get(path));
    // Incompatible types!
    if (!rawType.isInstance(propertyValue)) {
      stack.attach("The property value in '" + path
          + "' obtained isn't an instance of " + type.getType() + ".");
      return null;
    }

    @SuppressWarnings("unchecked")
    T value = (T) propertyValue;
    return value;
  }

  /**
   * Gets the property path for the specified key,
   * if the key isn't qualified with &#64;Property,
   * returns null.
   */
  public static String getPropertyPath(Key<?> key) {
    for (Qualifier qualifier : key.getQualifiers()) {
      if (qualifier.raw() instanceof Property) {
        return ((Property) qualifier.raw()).value();
      }
    }
    return null;
  }

  public static Object tryConvert(Class<?> requiredType, Object object) {
    if (object == null) {
      return null;
    } else if (requiredType == String.class) {
      return String.valueOf(object);
    } else if (requiredType == Boolean.class) {
      String value = String.valueOf(object);
      if (value.equalsIgnoreCase("true")) {
        return true;
      } else if (value.equalsIgnoreCase("false")) {
        return false;
      }
    }
    return object;
  }

}
