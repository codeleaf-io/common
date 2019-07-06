package io.codeleaf.common.utils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.function.Supplier;

/**
 * This class provides method references that can be de-referenced to obtain the referenced method.
 *
 * @author tvburger@gmail.com
 * @since 0.1.0
 */
public final class MethodReferences {

    private MethodReferences() {
    }

    private static final ThreadLocal<Method> invokedMethod = new ThreadLocal<>();
    private static final InvocationHandler setInvokedMethodHandler = (proxy, method, args) -> setMethod(method);
    private static final Map<Class<?>, Object> proxyMap = new HashMap<>();

    /**
     * Creates a proxy object that can be used to create a method reference that can be resolved back to the original method.
     *
     * @param typeClass the interface defining the method
     * @param <T>       the type of the interface
     * @return a proxy object for the specified interface
     * @throws IllegalArgumentException if typeClass is not an interface
     * @throws NullPointerException     if typeClass is <code>null</code>
     */
    @SuppressWarnings("unchecked")
    public static <T> T createProxy(Class<T> typeClass) {
        Objects.requireNonNull(typeClass);
        if (!typeClass.isInterface()) {
            throw new IllegalArgumentException("Must be interface!");
        }
        List<Class<?>> interfaces = new ArrayList<>();
        interfaces.add(typeClass);
        interfaces.addAll(Arrays.asList(typeClass.getInterfaces()));
        return (T) Proxy.newProxyInstance(typeClass.getClassLoader(), interfaces.toArray(new Class<?>[0]), setInvokedMethodHandler);
    }

    /**
     * Returns a proxy object that can be used to create a method reference that can be resolved back to the original method.
     * If a proxy object for the specific type was already created, it is being reused.
     *
     * @param typeClass the interface defining the method
     * @param <T>       the type of the interface
     * @return a proxy object for the specified interface
     * @throws IllegalArgumentException if typeClass is not an interface
     * @throws NullPointerException     if typeClass is <code>null</code>
     */
    @SuppressWarnings("unchecked")
    public static <T> T getProxy(Class<T> typeClass) {
        Objects.requireNonNull(typeClass);
        return (T) proxyMap.computeIfAbsent(typeClass, MethodReferences::createProxy);
    }

    public static Supplier<?> reference(Method method) {
        return (Supplier<Object>) () -> setMethod(method);
    }

    /**
     * Returns the method that was reference by the supplier.
     *
     * @param methodReference a method reference created by {@link #createProxy(Class)} or {@link #getProxy(Class)}
     * @return the Method that was referenced
     * @throws IllegalArgumentException if the methodReference was not obtained through a proxy
     * @throws NullPointerException     if methodReference is <code>null</code>
     */
    public static Method derefence(Supplier<?> methodReference) {
        Objects.requireNonNull(methodReference);
        try {
            methodReference.get();
            Method method = invokedMethod.get();
            if (method == null) {
                throw new IllegalArgumentException("Could not determine method: methodReference must be obtained using MethodReferences.get(Type.class)::method");
            }
            return method;
        } finally {
            invokedMethod.remove();
        }
    }

    private static Object getReturnValue(Method method) {
        Class<?> returnType = method.getReturnType();
        Object returnValue;
        if (returnType.isPrimitive()) {
            switch (returnType.getName()) {
                case "boolean":
                    returnValue = false;
                    break;
                case "byte":
                    returnValue = (byte) 0x00;
                    break;
                case "char":
                    returnValue = (char) 0;
                    break;
                case "short":
                    returnValue = (short) 0;
                    break;
                case "int":
                    returnValue = 0;
                    break;
                case "long":
                    returnValue = 0L;
                    break;
                case "float":
                    returnValue = 0.0f;
                    break;
                case "double":
                    returnValue = 0.0;
                    break;
                case "void":
                    returnValue = null;
                    break;
                default:
                    throw new IllegalStateException("Unknown return type: " + returnType);
            }
        } else {
            returnValue = null;
        }
        return returnValue;
    }

    private static Object setMethod(Method method) {
        invokedMethod.set(method);
        return getReturnValue(method);
    }
}
