package ru.otus.cachehw;

@FunctionalInterface
public interface HwListener<K, V> {
    void notify(K key, V value, String action);
}
