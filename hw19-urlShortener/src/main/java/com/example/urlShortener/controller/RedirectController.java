package com.example.urlShortener.controller;

import com.example.urlShortener.service.LinkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
@Slf4j
public class RedirectController {
    private final LinkService linkService;

    @GetMapping("/r/{shortCode}")
    public ResponseEntity<Void> redirectToOriginal(@PathVariable String shortCode) {
        log.info("Redirect request for: {}", shortCode);

        var originalUrl = linkService.getOriginalUrl(shortCode);

        if (originalUrl.isEmpty()) {
            log.warn("Short link not found: {}", shortCode);
            return ResponseEntity.notFound().build();
        }

        // Инкрементируем счетчик (автоматически инвалидирует кеш благодаря @CacheEvict)
        linkService.incrementClickCount(shortCode);

        String url = originalUrl.get();
        log.info("Redirecting {} to {}", shortCode, url.length() > 100 ? url.substring(0, 100) + "..." : url);

        return ResponseEntity.status(302)
                .header("Location", url)
                .header("X-Cache-Status", "redirected")
                .build();
    }
}
