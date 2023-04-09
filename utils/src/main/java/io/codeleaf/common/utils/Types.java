package io.codeleaf.common.utils;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;

public final class Types {

    private Types() {
    }

    public static Type cast(Class<?> rawType, Type... actualTypeArguments) {
        return ParameterizedTypeImpl.create(rawType, actualTypeArguments);
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

    public static boolean definesMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
        return definesMethod(clazz, false, name, parameterTypes);
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

    public static final class ParameterizedTypeImpl implements ParameterizedType {

        private final Type ownerType;
        private final Class<?> rawType;
        private final Type[] actualTypeArguments;

        private ParameterizedTypeImpl(Type ownerType, Class<?> rawType, Type[] actualTypeArguments) {
            this.ownerType = ownerType;
            this.rawType = rawType;
            this.actualTypeArguments = actualTypeArguments;
        }

        public static ParameterizedTypeImpl create(Class<?> rawType, Type... actualTypeArguments) {
            return create(rawType, rawType.getDeclaringClass(), actualTypeArguments);
        }

        public static ParameterizedTypeImpl create(Type ownerType, Class<?> rawType, Type... actualTypeArguments) {
            Objects.requireNonNull(ownerType);
            Objects.requireNonNull(rawType);
            int expectedTypeArguments = rawType.getTypeParameters().length;
            if (actualTypeArguments == null && expectedTypeArguments != 0 ||
                    actualTypeArguments != null && expectedTypeArguments != actualTypeArguments.length) {
                throw new MalformedParameterizedTypeException();
            }
            return new ParameterizedTypeImpl(ownerType, rawType, actualTypeArguments == null ? new Type[0] :
                    Arrays.copyOf(actualTypeArguments, actualTypeArguments.length));
        }

        @Override
        public Type getOwnerType() {
            return ownerType;
        }

        @Override
        public Class<?> getRawType() {
            return rawType;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return actualTypeArguments.clone();
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object instanceof ParameterizedType) {
                ParameterizedType other = (ParameterizedType) object;
                return Objects.equals(ownerType, other.getOwnerType()) &&
                        Objects.equals(rawType, other.getRawType()) &&
                        Arrays.equals(actualTypeArguments, other.getActualTypeArguments());
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return Objects.hash(rawType, Arrays.hashCode(actualTypeArguments), ownerType);
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            if (ownerType != null) {
                builder.append(ownerType.getTypeName());
                builder.append("$");
                if (ownerType instanceof ParameterizedTypeImpl) {
                    builder.append(rawType.getName().replace(
                            ((ParameterizedTypeImpl) ownerType).rawType.getName() + "$",
                            ""));
                } else {
                    builder.append(rawType.getSimpleName());
                }
            } else {
                builder.append(rawType.getName());
            }
            if (actualTypeArguments != null) {
                StringJoiner joiner = new StringJoiner(", ", "<", ">");
                joiner.setEmptyValue("");
                for (Type t : actualTypeArguments) {
                    joiner.add(t.getTypeName());
                }
                builder.append(joiner);
            }
            return builder.toString();
        }
    }
}
