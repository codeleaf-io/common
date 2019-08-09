package io.codeleaf.common.utils;

import io.codeleaf.common.behaviors.Registry;
import io.codeleaf.common.behaviors.impl.DefaultRegistry;

import java.util.Set;

public final class Registries {

    private static final Registry EMPTY_REGISTRY = unmodifiableRegistry(new DefaultRegistry());

    private Registries() {
    }

    public static Registry emptyRegistry() {
        return EMPTY_REGISTRY;
    }

    public static Registry unmodifiableRegistry(Registry registry) {
        return new Registry() {
            @Override
            public <T> T lookup(String name, Class<T> objectType) {
                return registry.lookup(name, objectType);
            }

            @Override
            public Set<String> getNames() {
                return registry.getNames();
            }

            @Override
            public Set<String> getNames(Class<?> objectType) {
                return registry.getNames(objectType);
            }

            @Override
            public Object lookup(String name) {
                return registry.lookup(name);
            }

            @Override
            public <T> boolean contains(String name, Class<T> objectType) {
                return registry.contains(name, objectType);
            }

            @Override
            public boolean contains(String name) {
                return registry.contains(name);
            }

            @Override
            public void register(String name, Object object) {
                throw new UnsupportedOperationException();
            }
        };
    }
}
