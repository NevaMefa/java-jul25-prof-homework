package ru.otus.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.cachehw.HwCache;
import ru.otus.cachehw.MyCache;
import ru.otus.model.Client;

public class GcDropDemo {
    private static final Logger logger = LoggerFactory.getLogger(GcDropDemo.class);

    public static void main(String[] args) {
        HwCache<String, Client> cache = new MyCache<>();

        for (long i = 0; i < 300_000; i++) {
            String key = String.valueOf(i); // Преобразуем Long в String
            cache.put(key, new Client(i, "N" + i, null, List.of()));
            if (i % 50_000 == 0) System.out.println("put " + i);
        }

        System.gc();
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            logger.error("Exception occurred: ", e);
        }

        for (long probe : List.of(0L, 50_000L, 100_000L, 200_000L, 250_000L)) {
            String key = String.valueOf(probe);
            System.out.println("id=" + probe + " -> " + (cache.get(key) == null ? "collected" : "present"));
        }
    }
}
