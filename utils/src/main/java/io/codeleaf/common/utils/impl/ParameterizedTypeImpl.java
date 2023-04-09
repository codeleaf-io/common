package io.codeleaf.common.utils.impl;

import java.lang.reflect.MalformedParameterizedTypeException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;

public final class ParameterizedTypeImpl implements ParameterizedType {

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
        return new ParameterizedTypeImpl(ownerType, rawType,
                actualTypeArguments == null ? new Type[0] :
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
