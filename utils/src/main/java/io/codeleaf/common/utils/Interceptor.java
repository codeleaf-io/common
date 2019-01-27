package io.codeleaf.common.utils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public final class Interceptor implements InvocationHandler {

    private final Object target;
    private final List<MethodBinding> methodBindings;

    private Interceptor(Object target, List<MethodBinding> methodBindings) {
        this.target = target;
        this.methodBindings = methodBindings;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MethodBinding methodBinding = getMethodBinding(method);
        return methodBinding == null ? method.invoke(target, args) : methodBinding.invoke(args);
    }

    public void addBinding(MethodBinding methodBinding) {
        Objects.requireNonNull(methodBinding);
        methodBindings.add(methodBinding);
    }

    private MethodBinding getMethodBinding(Method method) {
        for (MethodBinding methodBinding : methodBindings) {
            if (methodBinding.equals(method)) {
                return methodBinding;
            }
        }
        return null;
    }

    public static <T> T create(Class<T> interfaceType, Object target) {
        return Types.cast(Proxy.newProxyInstance(interfaceType.getClassLoader(), new Class<?>[]{interfaceType}, new Interceptor(target, new LinkedList<>())));
    }

    public static void intercept(Object target, String methodName, Class<?>[] parameterTypes, Function<Object[], Object> function) {
        Objects.requireNonNull(target);
        Objects.requireNonNull(methodName);
        if (methodName.isEmpty()) {
            throw new IllegalArgumentException();
        }
        Objects.requireNonNull(function);
        if (!Proxy.isProxyClass(target.getClass())) {
            throw new IllegalArgumentException();
        }
        InvocationHandler handler = Proxy.getInvocationHandler(target);
        if (!(handler instanceof Interceptor)) {
            throw new IllegalArgumentException();
        }
        ((Interceptor) handler).addBinding(new MethodBinding(methodName, parameterTypes, function));
    }

    public static class MethodBinding {

        private final String methodName;
        private final Class<?>[] parameterTypes;
        private final Function<Object[], Object> function;

        public MethodBinding(String methodName, Class<?>[] parameterTypes, Function<Object[], Object> function) {
            this.methodName = methodName;
            this.parameterTypes = parameterTypes;
            this.function = function;
        }

        public String getMethodName() {
            return methodName;
        }

        public Class<?>[] getParameterTypes() {
            return parameterTypes;
        }

        public Function<Object[], Object> getFunction() {
            return function;
        }

        public Object invoke(Object[] arguments) {
            return function.apply(arguments);
        }

        public boolean equals(Method method) {
            return methodName.equals(method.getName()) && methodArgsEquals(method.getParameterTypes());
        }

        private boolean methodArgsEquals(Class<?>[] parameterTypes) {
            return parameterTypes == null && (this.parameterTypes == null || this.parameterTypes.length == 0)
                    || parameterTypes != null && parameterTypes.length == 0 && (this.parameterTypes == null || this.parameterTypes.length == 0)
                    || Arrays.equals(parameterTypes, this.parameterTypes);
        }
    }
}
