package io.codeleaf.common.behaviors;

import java.util.Set;

public interface Registry {

    <T> T lookup(String name, Class<T> objectType);

    Set<String> getNames();

    Set<String> getNames(Class<?> objectType);

    Object lookup(String name);

    <T> boolean contains(String name, Class<T> objectType);

    boolean contains(String name);

    void register(String name, Object object);

}
