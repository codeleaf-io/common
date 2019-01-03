package io.codeleaf.common.utils;

import java.util.Iterator;
import java.util.Objects;
import java.util.ServiceLoader;

/**
 * This class loads a specific service leveraging the ServiceLoader.
 *
 * @author tvburger@gmail.com
 * @since 0.1.0
 */
public final class SingletonServiceLoader {

    /**
     * Loads the specified service, for which exactly one service must be registered.
     *
     * @param serviceClass the class of the service type
     * @param <T>          the type of the service
     * @return the loaded service
     * @throws IllegalStateException if not exactly one service is registered
     */
    public static <T> T load(Class<T> serviceClass) {
        return load(serviceClass, false);
    }

    /**
     * Loads the specified service or returns the default service, for which at most one service can be registered.
     *
     * @param serviceClass   the class of the service type
     * @param defaultService the default service to return if none were registered
     * @param <T>            the type of the service
     * @return the loaded service or the default service if none were registered
     * @throws IllegalStateException if multiple services are registered
     */
    public static <T> T load(Class<T> serviceClass, T defaultService) {
        return load(serviceClass, defaultService, false);
    }

    /**
     * Loads the specified service, or optionally throws an exception if multiple services are registered.
     *
     * @param serviceClass     the class of the service type
     * @param ignoreDuplicates if <code>false</code>, only a single service of the given type can be registered
     * @param <T>              the type of the service
     * @return the loaded service
     * @throws IllegalStateException if <code>ignoreDuplicates</code> is <code>false</code> and multiple services of the given type are registered or no services has been registered
     */
    public static <T> T load(Class<T> serviceClass, boolean ignoreDuplicates) {
        Objects.requireNonNull(serviceClass);
        T service = load(serviceClass, null, ignoreDuplicates);
        if (service == null) {
            throw new IllegalStateException("No service registered for: " + serviceClass.getName());
        }
        return service;
    }

    /**
     * Loads the specified service, returns the default service if none registered, or optionally throws an exception if multiple services are registered.
     *
     * @param serviceClass     the class of the service type
     * @param defaultService   the default service to return if none were registered
     * @param ignoreDuplicates if <code>false</code>, only a single service of the given type can be registered
     * @param <T>              the type of the service
     * @return the loaded service or the default service if none were registered
     * @throws IllegalStateException if <code>ignoreDuplicates</code> is <code>false</code> and multiple services of the given type are registered
     */
    public static <T> T load(Class<T> serviceClass, T defaultService, boolean ignoreDuplicates) {
        Objects.requireNonNull(serviceClass);
        ServiceLoader<T> loader = ServiceLoader.load(serviceClass);
        Iterator<T> iterator = loader.iterator();
        T service;
        if (!iterator.hasNext()) {
            service = defaultService;
        } else {
            service = iterator.next();
            if (!ignoreDuplicates && iterator.hasNext()) {
                throw new IllegalStateException("Multiple services registered for: " + serviceClass.getName());
            }
        }
        return service;
    }

    private SingletonServiceLoader() {
    }

}
