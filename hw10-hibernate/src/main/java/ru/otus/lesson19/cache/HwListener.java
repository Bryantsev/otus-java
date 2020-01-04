package ru.otus.lesson19.cache;

/**
 * @author sergey
 * created on 14.12.18.
 */
public interface HwListener<K, V> {
    void notify(K key, V value, String action);
}
