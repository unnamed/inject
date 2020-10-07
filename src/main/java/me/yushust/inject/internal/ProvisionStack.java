package me.yushust.inject.internal;

import me.yushust.inject.error.ErrorAttachableImpl;
import me.yushust.inject.key.Key;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

class ProvisionStack extends ErrorAttachableImpl {

  // Used to invoke an O(1) "get" method
  private final Map<Key<?>, Object> values =
      new HashMap<>();
  // The real provision Stack, contains a relation of
  private final LinkedList<KeyInstanceEntry<?>> stack =
      new LinkedList<>();

  public boolean has(Key<?> key) {
    return values.containsKey(key);
  }

  public <T> T get(Key<T> key) {
    // the cast is safe, the
    // map is modified only with the
    // generic method ProvisionStack#add(...)
    @SuppressWarnings("unchecked")
    T value = (T) values.get(key);
    return value;
  }

  public void removeFirst() {
    Map.Entry<Key<?>, Object> entry = stack.removeFirst();
    if (entry != null) {
      values.remove(entry.getKey());
    }
  }

  public <T> void add(Key<T> key, T value) {
    values.put(key, value);
    stack.addFirst(new KeyInstanceEntry<>(key, value));
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    Iterator<KeyInstanceEntry<?>> entries = stack.iterator();
    while (entries.hasNext()) {
      KeyInstanceEntry<?> entry = entries.next();
      builder.append(entry.getKey());
      if (entries.hasNext()) {
        builder.append(" -> ");
      }
    }
    return builder.toString();
  }

  private static class KeyInstanceEntry<T> implements Map.Entry<Key<?>, Object> {

    private final Key<T> key;
    private final T value;

    public KeyInstanceEntry(Key<T> key, T value) {
      this.key = key;
      this.value = value;
    }

    public Key<?> getKey() {
      return key;
    }

    public Object getValue() {
      return value;
    }

    public T setValue(Object value) {
      // it's not really handled like
      // an entry, it's used like a javafx.util.Pair
      throw new UnsupportedOperationException("This entry is immutable!");
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      KeyInstanceEntry<?> that = (KeyInstanceEntry<?>) o;
      return key.equals(that.key) &&
          value.equals(that.value);
    }

    @Override
    public int hashCode() {
      int result = 1;
      result = 31 * result + key.hashCode();
      result = 31 * result + value.hashCode();
      return result;
    }
  }

}
