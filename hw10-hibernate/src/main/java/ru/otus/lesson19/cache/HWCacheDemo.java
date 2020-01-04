package ru.otus.lesson19.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author sergey
 * created on 14.12.18.
 */
public class HWCacheDemo {
    private static final Logger logger = LoggerFactory.getLogger(HWCacheDemo.class);

    public static void main(String[] args) {
        new HWCacheDemo().demo();
    }

    private void demo() {
        HwCache<Integer, Integer> cache = new MyCache<>("integer");
        HwListener<Integer, Integer> listener =
            (key, value, action) -> logger.info("key: {}, value: {}, action: {}", key, value, action);
        cache.addListener(listener);
        cache.put(1, 1);
        cache.remove(1);
        int limit = 1000;
        for (int idx = 0; idx < limit; idx++) {
            cache.put(idx, idx);
        }
        // Проверим наличие значений до явного вызова gc
        for (int idx = 0; idx < limit; idx++) {
            cache.get(idx);
        }
        System.gc();
        logger.info("*** After gc ***");
        // Проверим наличие значений после вызова gc
        for (int idx = 0; idx < limit; idx++) {
            cache.get(idx);
        }

        cache.removeListener(listener);
    }
}
