package me.yushust.inject;

import me.yushust.inject.internal.BinderImpl;
import me.yushust.inject.internal.InjectorImpl;
import me.yushust.inject.resolve.MembersBox;
import me.yushust.inject.resolve.MembersBoxImpl;
import me.yushust.inject.resolve.QualifierFactory;

import java.util.Arrays;

public final class InjectorFactory {

  private InjectorFactory() {
    throw new UnsupportedOperationException("This class couldn't be instantiated!");
  }

  public static Injector create(Module... modules) {
    return create(Arrays.asList(modules));
  }

  public static Injector create(Iterable<? extends Module> modules) {
    QualifierFactory qualifierFactory = null;
    MembersBox membersBox = new MembersBoxImpl(qualifierFactory);
    BinderImpl binder = new BinderImpl(qualifierFactory, membersBox);
    binder.install(modules);
    binder.reportAttachedErrors();
    return new InjectorImpl(membersBox, binder);
  }

}
