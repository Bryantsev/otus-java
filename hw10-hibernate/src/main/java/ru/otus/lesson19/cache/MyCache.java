package ru.otus.lesson19.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author sergey
 * created on 14.12.18.
 */
public class MyCache<K, V> implements HwCache<K, V> {

    private static Logger logger = LoggerFactory.getLogger(MyCache.class);

    private final Map<K, V> cache = new WeakHashMap<>();
    private final List<HwListener<K, V>> listeners = new LinkedList<>();

    private String name;
    private boolean enabled;

    public MyCache(String name) {
        initCache(name, true);
    }

    public MyCache(String name, boolean enabled) {
        initCache(name, enabled);
    }

    private void initCache(String name, boolean enabled) {
        this.name = name;
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void put(K key, V value) {
        if (!enabled) {
            return;
        }

        cache.put(key, value);
        notifyListeners(key, value, "put");
    }

    @Override
    public void remove(K key) {
        if (!enabled) {
            return;
        }

        V value = cache.get(key);
        cache.remove(key);
        notifyListeners(key, value, "remove");
    }

    @Override
    public V get(K key) {
        if (!enabled) {
            return null;
        }
        V value = cache.get(key);
        notifyListeners(key, value, "get");
        return value;
    }

    @Override
    public void addListener(HwListener<K, V> listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(HwListener<K, V> listener) {
        listeners.remove(listener);
    }

    private void notifyListeners(K key, V value, String action) {
        for (HwListener<K, V> listener : listeners) {
            listener.notify(key, value, action);
        }
    }
}
