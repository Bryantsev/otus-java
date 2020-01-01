package ru.otus.lesson19.cache;

/**
 * @author sergey
 * created on 14.12.18.
 */
public interface HwCache<K, V> {

    String getName();

    void put(K key, V value);

    void remove(K key);

    V get(K key);

    void addListener(HwListener<K, V> listener);

    void removeListener(HwListener<K, V> listener);
}
