package ru.otus.cachehw;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

public class MyCache<K, V> implements HwCache<K, V> {

    private final Map<K, V> storage = Collections.synchronizedMap(new WeakHashMap<>());
    private final Set<HwListener<K, V>> listeners = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @Override
    public void put(K key, V value) {
        if (key == null) return;
        storage.put(key, value);
        notifyAll(key, value, "put");
    }

    @Override
    public void remove(K key) {
        if (key == null) return;
        V old = storage.remove(key);
        notifyAll(key, old, "remove");
    }

    @Override
    public V get(K key) {
        if (key == null) return null;
        V v = storage.get(key);
        notifyAll(key, v, "get");
        return v;
    }

    @Override
    public void addListener(HwListener<K, V> listener) {
        if (listener != null) listeners.add(listener);
    }

    @Override
    public void removeListener(HwListener<K, V> listener) {
        if (listener != null) listeners.remove(listener);
    }

    private void notifyAll(K key, V value, String action) {
        for (var l : listeners) {
            try {
                l.notify(key, value, action);
            } catch (Exception ignored) {
            }
        }
    }
}
