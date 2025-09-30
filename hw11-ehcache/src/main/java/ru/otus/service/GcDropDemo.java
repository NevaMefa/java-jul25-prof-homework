package ru.otus.service;

import java.util.List;
import ru.otus.cachehw.HwCache;
import ru.otus.cachehw.MyCache;
import ru.otus.model.Client;

public class GcDropDemo {
    public static void main(String[] args) {
        HwCache<Long, Client> cache = new MyCache<>();

        for (long i = 0; i < 300_000; i++) {
            cache.put(i, new Client(i, "N" + i, null, List.of()));
            if (i % 50_000 == 0) System.out.println("put " + i);
        }

        System.gc();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }

        for (long probe : List.of(0L, 50_000L, 100_000L, 200_000L, 250_000L)) {
            System.out.println("id=" + probe + " -> " + (cache.get(probe) == null ? "collected" : "present"));
        }
    }
}
