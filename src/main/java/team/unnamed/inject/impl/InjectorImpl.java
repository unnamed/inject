/*
 * This file is part of inject, licensed under the MIT license
 *
 * Copyright (c) 2021-2023 Unnamed Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package team.unnamed.inject.impl;

import team.unnamed.inject.Injector;
import team.unnamed.inject.Provider;
import team.unnamed.inject.error.InjectionException;
import team.unnamed.inject.key.InjectedKey;
import team.unnamed.inject.key.Key;
import team.unnamed.inject.key.TypeReference;
import team.unnamed.inject.provision.StdProvider;
import team.unnamed.inject.resolve.ComponentResolver;
import team.unnamed.inject.resolve.solution.InjectableConstructor;
import team.unnamed.inject.resolve.solution.InjectableMember;
import team.unnamed.inject.util.Validate;

import java.util.List;

public class InjectorImpl implements Injector {

    public static final Object ABSENT_INSTANCE
            = new Object();
    // The provision stack is a sensible part
    // of the library, you must take care while
    // handling a ProvisionStack, you can cause
    // a StackOverflowError if the stack is
    // accidentally removed from the thread
    protected final ThreadLocal<ProvisionStack> provisionStackThreadLocal =
            new ThreadLocal<>();
    private final ProvisionHandle provisionHandle;

    private final BinderImpl binder;

    public InjectorImpl(BinderImpl binder) {
        this.binder = Validate.notNull(binder);
        this.provisionHandle = new ProvisionHandle(this, binder);
    }

    public Object getValue(
            InjectedKey<?> key,
            ProvisionStack stack
    ) {
        // We don't need to clone the stack,
        // the type-instance relations are
        // removes automatically when ended
        // with the injection
        List<String> snapshot = stack.getErrorMessages();
        Object value = getInstance(stack, key.getKey(), true);
        if (value == null && !key.isOptional()) {
            return ABSENT_INSTANCE;
        } else if (key.isOptional()) {
            // remove errors because the injection
            // is optional and we don't need a report
            // of fails that can be valid
            stack.applySnapshot(snapshot);
        }
        return value;
    }

    /**
     * Invokes the overloaded method
     * {@link InjectorImpl#getInstance(ProvisionStack, Key, boolean)}
     * passing an empty provision stack
     */
    @Override
    public <T> T getInstance(TypeReference<T> type) {
        boolean stackWasNotPresent = provisionStackThreadLocal.get() == null;
        // The creation of a new provision stack indicates
        // the manual call of getInstance() or injectMembers(),
        // the type cannot be a key. Keys are used for injectable
        // members, not for manually call a inject method
        T value = getInstance(stackForThisThread(), Key.of(type), true);
        // We need to clear the stack
        // after a manual injection,
        // the stack is only cleared if initially
        // it was null, if not, it is possibly
        // being used
        if (stackWasNotPresent) {
            removeStackFromThisThread();
        }
        return value;
    }

    /**
     * Delegates the functionality to the abstract method
     * {@link InjectorImpl#injectMembers(ProvisionStack, Key, Object)}
     * passing an empty provision stack
     */
    @Override
    public <T> void injectMembers(TypeReference<T> type, T instance) {
        boolean stackWasNotPresent = provisionStackThreadLocal.get() == null;
        // The creation of a new provision stack indicates
        // the manual call of getInstance() or injectMembers(),
        // the type cannot be a key. Keys are used for injectable
        // members, not for manually call a inject method
        injectMembers(stackForThisThread(), Key.of(type), instance);
        // We need to clear the stack
        // after a manual injection,
        // the stack is only cleared if initially
        // it was null, if not, it is possibly
        // being used
        if (stackWasNotPresent) {
            removeStackFromThisThread();
        }
    }

    /**
     * @return The provision stack in the thread local, if
     * not present, creates a provision stack and stores
     * it in the thread local
     */
    public ProvisionStack stackForThisThread() {
        ProvisionStack stack = provisionStackThreadLocal.get();
        // the stack doesn't exist, create a new stack
        // and set to the thread local
        if (stack == null) {
            stack = new ProvisionStack();
            provisionStackThreadLocal.set(stack);
        }
        return stack;
    }

    /**
     * Sets the provision stack in this thread to null,
     * so it should be re-started with {@link InjectorImpl#stackForThisThread}.
     *
     * <p>Simple explanation: There's one {@link ProvisionStack}
     * per thread. This is handled using a {@link ThreadLocal}</p>
     */
    protected void removeStackFromThisThread() {
        ProvisionStack stack = provisionStackThreadLocal.get();
        provisionStackThreadLocal.set(null);
        if (stack != null && stack.hasErrors()) {
            throw new InjectionException(stack.formatMessages());
        }
    }

    @Override
    public <T> Provider<? extends T> getProvider(TypeReference<T> key) {
        return binder.getProvider(Key.of(key));
    }

    @Override
    public void injectStaticMembers(Class<?> clazz) {
        boolean stackWasNotPresent = provisionStackThreadLocal.get() == null;
        injectMembers(stackForThisThread(), Key.of(TypeReference.of(clazz)), null);
        if (stackWasNotPresent) {
            removeStackFromThisThread();
        }
    }

    public <T> void injectMembers(ProvisionStack stack, Key<T> type, T instance) {
        if (instance != null) {
            stack.push(type, instance);
        }
        for (InjectableMember member : ComponentResolver.fields().get(type.getType())) {
            member.inject(this, stack, instance);
        }
        for (InjectableMember member : ComponentResolver.methods().get(type.getType())) {
            member.inject(this, stack, instance);
        }
        if (instance != null) {
            stack.pop();
        }
    }

    public <T> T getInstance(Key<T> type, boolean useExplicitBindings) {
        return getInstance(stackForThisThread(), type, useExplicitBindings);
    }

    public <T> T getInstance(ProvisionStack stack, Key<T> type, boolean useExplicitBindings) {
        Class<? super T> rawType = type.getType().getRawType();
        // Default injections
        if (
                rawType == Injector.class
                        || rawType == InjectorImpl.class
        ) {
            @SuppressWarnings("unchecked")
            T value = (T) this;
            return value;
        }

        //String path = PropertyRequestHandle.getPropertyPath(type);
        //if (path != null) {
        //  return PropertyRequestHandle.getProperty(path, type.getType(), provisionHandle, stack);
        //}

        // If the stack isn't empty, it's a recursive call.
        // If the key is present in the provision stack,
        // we can return the stored instance instead of creating
        // another instance
        if (stack.has(type)) {
            // It's a cyclic dependency and it's now fixed
            return stack.get(type);
        }

        AnnotationScanner.bind(type.getType(), binder);
        AnnotationScanner.scope(type.getType(), binder);
        if (useExplicitBindings) {
            StdProvider<T> provider = provisionHandle.getProviderAndInject(stack, type);
            if (provider != null) {
                return provider.get(type);
            }
        }

        InjectableConstructor constructor = ComponentResolver.constructor().get(stack, type.getType());
        if (constructor == null) { // the errors are thrown by the caller
            return null;
        }

        Object instance = constructor.inject(
                this,
                stack,
                null
        );

        @SuppressWarnings("unchecked")
        T value = (T) instance;

        if (value != null) {
            injectMembers(stack, type, value);
        }

        return value;
    }

}
