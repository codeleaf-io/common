package io.codeleaf.common.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class Methods {

    private Methods() {
    }

    public static Object invoke(Method method, Object object) {
        try {
            return method.invoke(object);
        } catch (IllegalAccessException | InvocationTargetException cause) {
            throw new IllegalArgumentException(cause);
        }
    }

    public static <T extends Annotation> boolean hasAnnotation(Method method, Class<T> annotationClass) {
        return method.getAnnotation(annotationClass) != null;
    }

    public static boolean isSupplier(Method method) {
        return method.getParameterTypes().length == 0 && !method.getReturnType().equals(Void.class);
    }
}
