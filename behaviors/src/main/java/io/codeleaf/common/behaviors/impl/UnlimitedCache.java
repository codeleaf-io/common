package io.codeleaf.common.behaviors.impl;

import io.codeleaf.common.behaviors.Cache;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Provides a cache implementation without any limitations on memory usage. Internally leverages a <code>HashMap</code>.
 *
 * @param <K> the type of the keys in the cache
 * @param <V> the type of the values in the cache
 * @author tvburger@gmail.com
 * @since 0.1.0
 */
public final class UnlimitedCache<K, V> implements Cache<K, V> {

    private final Map<K, V> cache = new HashMap<>();

    @Override
    public void put(K key, V value) {
        cache.put(key, value);
    }

    @Override
    public V get(K key) {
        if (!cache.containsKey(key)) {
            throw new NoSuchElementException();
        }
        return cache.get(key);
    }

    @Override
    public boolean has(K key) {
        return cache.containsKey(key);
    }

    @Override
    public void remove(K key) {
        cache.remove(key);
    }

    @Override
    public int size() {
        return cache.size();
    }

}
