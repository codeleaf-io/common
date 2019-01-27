package io.codeleaf.common.utils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Objects;

public final class Types {

    private Types() {
    }

    @SuppressWarnings("unchecked")
    public static <T> T cast(Object instance) {
        return (T) instance;
    }

    public static <T> T cast(Object instance, Class<T> interfaceType) {
        Objects.requireNonNull(instance);
        Objects.requireNonNull(interfaceType);
        if (!interfaceType.isInterface()) {
            throw new IllegalArgumentException("Must be an interface!");
        }
        Object implementingInstance;
        if (interfaceType.isAssignableFrom(instance.getClass())) {
            implementingInstance = instance;
        } else {
            implementingInstance = Proxy.newProxyInstance(
                    interfaceType.getClassLoader(),
                    new Class<?>[]{interfaceType},
                    new ForwardHandler(instance));
        }
        return cast(implementingInstance);
    }

    public static boolean definesMethod(Class<?> clazz, Method method) {
        for (Method availableMethod : clazz.getMethods()) {
            if (availableMethod.getName().equals(method.getName())
                    && Arrays.equals(availableMethod.getParameterTypes(), method.getParameterTypes())) {
                return true;
            }
        }
        return false;
    }

    private static final class ForwardHandler implements InvocationHandler {

        private final Object target;

        ForwardHandler(Object target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Object invocationTarget;
            if (definesMethod(target.getClass(), method)) {
                invocationTarget = target;
            } else if (method.isDefault()) {
                invocationTarget = proxy;
            } else {
                throw new NoSuchMethodException("Method not found: " + method);
            }
            return method.invoke(invocationTarget, args);
        }
    }
}
