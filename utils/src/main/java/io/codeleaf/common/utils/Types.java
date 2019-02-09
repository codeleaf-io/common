package io.codeleaf.common.utils;

import java.lang.reflect.*;
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
        return definesMethod(clazz, Modifier.isStatic(method.getModifiers()), method.getName(), method.getParameterTypes());
    }

    public static boolean definesStaticMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
        return definesMethod(clazz, true, name, parameterTypes);
    }

    public static boolean definesNonStaticMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
        return definesMethod(clazz, false, name, parameterTypes);
    }

    private static boolean definesMethod(Class<?> clazz, boolean isStatic, String name, Class<?>[] parameterTypes) {
        for (Method availableMethod : clazz.getMethods()) {
            if (availableMethod.getName().equals(name)
                    && methodArgsEquals(parameterTypes, availableMethod.getParameterTypes())
                    && isStatic == Modifier.isStatic(availableMethod.getModifiers())) {
                return true;
            }
        }
        return false;
    }

    // TODO: move to Methods
    public static boolean methodArgsEquals(Class<?>[] parameterTypes1, Class<?>[] parameterTypes2) {
        return (parameterTypes1 == null || parameterTypes1.length == 0) && (parameterTypes2 == null || parameterTypes2.length == 0)
                || Arrays.equals(parameterTypes1, parameterTypes2);
    }

    public static <T> Constructor<T> getConstructor(Class<T> typeClass, Class<?>... parameterTypes) throws NoSuchMethodException {
        // TODO: make real implementation, we need to look for superclasses too...
        if (parameterTypes == null || parameterTypes.length == 0) {
            return typeClass.getConstructor();
        }
        return typeClass.getConstructor(parameterTypes);
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
