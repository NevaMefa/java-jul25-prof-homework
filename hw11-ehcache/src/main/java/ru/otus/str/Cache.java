package ru.otus.str;

import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cache {
    private static final Logger logger = LoggerFactory.getLogger(Cache.class);

    private final Map<String, Data> dataStore = Collections.synchronizedMap(new WeakHashMap<>());

    public static void main(String[] args) {
        var cache = new Cache();
        cache.fillCache();
        cache.go();
    }

    private void fillCache() {
        var data = new ArrayList<String>();
        for (int idx = 0; idx < 10; idx++) {
            data.add("v" + idx);
        }

        dataStore.put(new String("k1"), new Data(1, data));
        dataStore.put(new String("k2"), new Data(2, data));
        dataStore.put(new String("k3"), new Data(3, data));
    }

    private void go() {
        final Data d1 = dataStore.get("k1");
        DataProcessor.process(d1);

        final Data d2 = dataStore.get("k2");
        if (d2 != null) {
            logger.info("key:{}, values: {}", d2.getId(), d2.getValues());
        } else {
            logger.info("k2 was collected");
        }
    }
}
