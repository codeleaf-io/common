package io.codeleaf.common.behaviors.impl;

import io.codeleaf.common.behaviors.Registry;

import java.util.*;

public class DefaultRegistry implements Registry {

    private final Map<String, Object> objects = new HashMap<>();

    @Override
    public <T> T lookup(String name, Class<T> objectType) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(objectType);
        Object object = lookup(name);
        if (object == null) {
            return null;
        }
        if (!objectType.isAssignableFrom(object.getClass())) {
            throw new IllegalArgumentException();
        }
        return objectType.cast(object);
    }

    @Override
    public Set<String> getNames() {
        return Collections.unmodifiableSet(objects.keySet());
    }

    @Override
    public Set<String> getNames(Class<?> objectType) {
        Set<String> names = new LinkedHashSet<>();
        for (Map.Entry<String, Object> entry : objects.entrySet()) {
            if (objectType.isAssignableFrom(entry.getValue().getClass())) {
                names.add(entry.getKey());
            }
        }
        return names;
    }

    @Override
    public Object lookup(String name) {
        Objects.requireNonNull(name);
        return objects.get(name);
    }

    @Override
    public <T> boolean contains(String name, Class<T> objectType) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(objectType);
        return contains(name) && objectType.isAssignableFrom(lookup(name).getClass());
    }

    @Override
    public boolean contains(String name) {
        Objects.requireNonNull(name);
        return objects.containsKey(name);
    }

    @Override
    public void register(String name, Object object) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(object);
        if (objects.containsKey(name)) {
            throw new IllegalStateException("Already an object defined with name: " + name);
        }
        objects.put(name, object);
    }
}
