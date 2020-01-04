package ru.otus.lesson19.cache;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jmx.JmxReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HwCacheWithStats<K, V> implements HwCache<K, V> {

    private static Logger logger = LoggerFactory.getLogger(HwCacheWithStats.class);

    /**
     * Исходный кэш, над которым работает данная обертка со сбором статистики (шаблон фасад)
     */
    private HwCache<K, V> cache;
    private final MetricRegistry cacheMetrics = new MetricRegistry();
    private final JmxReporter reporter = JmxReporter.forRegistry(cacheMetrics).build();
    /**
     * Количество запросов значений в кэше
     */
    private Counter cacheRequests;
    /**
     * Количество найденных значений в кэше
     */
    private Counter cacheHits;
    /**
     * Название исходного кэша
     */
    private String name;


    public HwCacheWithStats(HwCache<K, V> cache) {
        this.cache = cache;
        name = cache.getName();
        resetStats();
        reporter.start();
    }

    /**
     * Сбросить статистику
     */
    public void resetStats() {
        cacheRequests = resetCounter("requests");
        cacheHits = resetCounter("hits");

        String metricName = MetricRegistry.name(this.name, "cache", "timer");
        cacheMetrics.remove(metricName);
    }

    private Counter resetCounter(String metricName) {
        String metricNameFull = MetricRegistry.name(this.name, "cache", metricName);
        cacheMetrics.remove(metricNameFull);
        return cacheMetrics.counter(metricNameFull);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void put(K key, V value) {
        cache.put(key, value);
    }

    @Override
    public void remove(K key) {
        cache.remove(key);
    }

    @Override
    public V get(K key) {
        cacheRequests.inc();
        V value = cache.get(key);
        if (value != null) {
            cacheHits.inc();
        }
        return value;
    }

    @Override
    public void addListener(HwListener<K, V> listener) {
        cache.addListener(listener);
    }

    @Override
    public void removeListener(HwListener<K, V> listener) {
        cache.removeListener(listener);
    }

    public Counter getCacheRequests() {
        return cacheRequests;
    }

    public Counter getCacheHits() {
        return cacheHits;
    }

}
