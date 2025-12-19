package com.example.urlShortener.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class LinkCache {
    private final Map<String, String> cache = new ConcurrentHashMap<>();

    public void put(String shortCode, String originalUrl) {
        cache.put(shortCode, originalUrl);
    }

    public String get(String shortCode) {
        return cache.get(shortCode);
    }
}
