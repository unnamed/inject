package me.yushust.inject.impl;

import me.yushust.inject.resolve.solution.InjectableConstructor;
import me.yushust.inject.resolve.solution.InjectableMember;
import me.yushust.inject.key.InjectedKey;

import java.lang.reflect.Modifier;
import java.util.List;

public class InjectionHandle {

  private final InjectorImpl injector;

  public InjectionHandle(InjectorImpl injector) {
    this.injector = injector;
  }

  public Object injectToMember(ProvisionStack stack,
                        Object instance,
                        InjectableMember member) {
    boolean isStatic = Modifier.isStatic(member.getMember().getModifiers());
    if (
        (instance == null && isStatic)
            || (instance != null && !isStatic)
    ) {
      List<InjectedKey<?>> keys = member.getKeys();
      Object[] values = getValuesForKeys(keys, member, stack);
      return member.inject(stack, instance, values);
    } else {
      return null;
    }
  }

  public Object[] getValuesForKeys(
      List<InjectedKey<?>> keys,
      Object member,
      ProvisionStack stack
  ) {
    Object[] values = new Object[keys.size()];
    for (int i = 0; i < keys.size(); i++) {
      InjectedKey<?> key = keys.get(i);
      // We don't need to clone the stack,
      // the type-instance relations are
      // removes automatically when ended
      // with the injection
      List<String> snapshot = stack.getErrorMessages();
      Object value = injector.getInstance(stack, key.getKey(), true);
      if (value == null && !key.isOptional()) {
        String at;
        if (member instanceof InjectableMember) {
          at = ((InjectableMember) member).getDeclaringType().toString();
        } else {
          at = ((InjectableConstructor) member).getMember().getDeclaringClass().getName();
        }
        stack.attach("Cannot inject '" + member + "' at '" + at + "':\n"
            + "    Reason: Cannot get an instance for key, and injection isn't optional\n"
            + "    Key: " + key.getKey());
      } else if (key.isOptional()) {
        // remove errors because the injection
        // is optional and we don't need a report
        // of fails that can be valid
        stack.applySnapshot(snapshot);
      }
      values[i] = value;
    }
    return values;
  }

}
