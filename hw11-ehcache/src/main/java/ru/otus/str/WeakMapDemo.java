package ru.otus.str;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WeakMapDemo {
    private static final Logger log = LoggerFactory.getLogger(WeakMapDemo.class);

    public static void main(String[] args) {
        Map<String, byte[]> cache = Collections.synchronizedMap(new WeakHashMap<>());

        for (int i = 0; i < 100_000; i++) {
            String key = new String("k" + i);
            cache.put(key, new byte[1024]); // ~1KB
            if (i % 10_000 == 0) {
                log.info("put {}", i);
            }
        }

        log.info("size before GC ~ {}", cache.size());

        System.gc();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }

        log.info("size after GC ~ {}", cache.size());
    }
}
