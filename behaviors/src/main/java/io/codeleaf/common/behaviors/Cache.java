package io.codeleaf.common.behaviors;

/**
 * Implementer behaves as a cache.
 *
 * @param <K> the type of the keys in the cache
 * @param <V> the type of the values in the cache
 * @author tvburger@gmail.com
 * @version %I%, %G%
 * @since 0.1.0
 */
public interface Cache<K, V> {

    /**
     * Puts the value in the cache under the specified key. If already present, the previous value is overwritten.
     *
     * @param key   the key used for retrieving the value from the cache
     * @param value the value to cache
     */
    void put(K key, V value);

    /**
     * Returns the cached value specified by the key.
     *
     * @param key the key used to retrieve the value
     * @return the cached value
     * @throws java.util.NoSuchElementException if no such key is present in the cache
     */
    V get(K key);

    /**
     * Returns <code>true</code> if a value has been put in the cache using the provided key.
     *
     * @param key the key
     * @return <code>true</code> if a value has been put in the cache using the provided key
     */
    boolean has(K key);

    /**
     * Removes the cached value under the given key. If they key was not present, no action is taken.
     *
     * @param key the key of the cache entry to remove
     */
    void remove(K key);

    /**
     * Returns the total amount of elements in the cache.
     *
     * @return the total amount of elements in the cache
     */
    int size();

}
