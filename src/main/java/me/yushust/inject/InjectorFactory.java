package me.yushust.inject;

import me.yushust.inject.internal.BinderImpl;
import me.yushust.inject.internal.DefaultQualifierFactory;
import me.yushust.inject.internal.InjectorImpl;
import me.yushust.inject.resolve.MembersResolver;
import me.yushust.inject.resolve.MembersResolverImpl;
import me.yushust.inject.resolve.QualifierFactory;

import java.util.Arrays;

public final class InjectorFactory {

  private InjectorFactory() {
    throw new UnsupportedOperationException();
  }

  public static Injector create(Module... modules) {
    return create(Arrays.asList(modules));
  }

  public static Injector create(Iterable<? extends Module> modules) {
    QualifierFactory qualifierFactory = DefaultQualifierFactory.INSTANCE;
    MembersResolver membersResolver = new MembersResolverImpl(qualifierFactory);
    BinderImpl binder = new BinderImpl(qualifierFactory, membersResolver);
    binder.install(modules);
    if (binder.hasErrors()) {
      binder.reportAttachedErrors();
    }
    return new InjectorImpl(membersResolver, binder);
  }

}
