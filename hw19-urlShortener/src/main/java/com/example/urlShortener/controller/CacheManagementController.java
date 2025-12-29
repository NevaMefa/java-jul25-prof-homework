package com.example.urlShortener.controller;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/cache")
@RequiredArgsConstructor
public class CacheManagementController {

    private final CacheManager cacheManager;

    @GetMapping("/info")
    public Map<String, Object> getCacheInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("availableCaches", cacheManager.getCacheNames());
        info.put("cacheCount", cacheManager.getCacheNames().size());
        return info;
    }

    @PostMapping("/clear/{cacheName}")
    public String clearCache(@PathVariable String cacheName) {
        var cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
            return "Cache '" + cacheName + "' cleared";
        }
        return "Cache not found";
    }
}
