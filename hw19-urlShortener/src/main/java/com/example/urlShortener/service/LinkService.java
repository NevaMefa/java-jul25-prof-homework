package com.example.urlShortener.service;

import com.example.urlShortener.dao.LinkRepository;
import com.example.urlShortener.dto.LinkResponse;
import com.example.urlShortener.entity.Link;
import com.example.urlShortener.util.CodeGenerator;
import io.micrometer.core.annotation.Timed;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@CacheConfig(cacheNames = {"links", "all-links", "popular-links"})
public class LinkService {

    private final LinkRepository linkRepository;
    private final CodeGenerator codeGenerator;

    @Timed(value = "link.create", description = "Time to create short link")
    @Transactional
    public LinkResponse createShortLink(String originalUrl) {
        log.info("Creating short link for: {}", originalUrl);

        String shortCode = codeGenerator.generateUniqueCode();
        log.info("Generated unique code: {}", shortCode);

        Link link = new Link(originalUrl, shortCode);
        Link savedLink = linkRepository.save(link);
        log.info("Link saved with ID: {}", savedLink.getId());

        // Кешируем оригинальный URL для быстрого доступа
        cacheOriginalUrl(shortCode, originalUrl);

        // Инвалидируем кеш всех ссылок
        clearAllLinksCache();

        return mapToResponse(savedLink);
    }

    // Отдельный метод для кеширования URL
    @CachePut(value = "links", key = "#shortCode")
    public String cacheOriginalUrl(String shortCode, String originalUrl) {
        return originalUrl;
    }

    @Timed(value = "link.resolve", description = "Time to resolve short URL")
    @Cacheable(value = "links", key = "#shortCode", unless = "#result == null")
    public Optional<String> getOriginalUrl(String shortCode) {
        log.debug("Cache MISS for: {}, querying database", shortCode);

        Optional<Link> linkOpt = linkRepository.findByShortCode(shortCode);

        if (linkOpt.isPresent()) {
            Link link = linkOpt.get();
            log.debug(
                    "Found in DB: {} -> {}",
                    shortCode,
                    link.getOriginalUrl().length() > 100
                            ? link.getOriginalUrl().substring(0, 100) + "..."
                            : link.getOriginalUrl());

            return Optional.of(link.getOriginalUrl());
        }

        log.warn("Short link not found: {}", shortCode);
        return Optional.empty();
    }

    @Timed(value = "link.get.all", description = "Time to get all links")
    @Cacheable(value = "all-links", unless = "#result.isEmpty()")
    public List<LinkResponse> getAllLinks() {
        log.debug("Loading all links from database");
        return linkRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Timed(value = "link.get.popular", description = "Time to get popular links")
    @Cacheable(value = "popular-links", key = "#limit")
    public List<LinkResponse> getTopPopular(int limit) {
        log.debug("Loading top {} popular links from database", limit);
        return linkRepository.findTopPopular(limit).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = "links", key = "#shortCode")
    public void incrementClickCount(String shortCode) {
        Optional<Link> linkOpt = linkRepository.findByShortCode(shortCode);
        if (linkOpt.isPresent()) {
            Link link = linkOpt.get();
            link.setClickCount(link.getClickCount() + 1);
            linkRepository.save(link);

            // Инвалидируем кеши со списками
            clearAllLinksCache();

            log.info("Click count updated for {}: {}", shortCode, link.getClickCount());
        }
    }

    @CacheEvict(value = "all-links")
    public void clearAllLinksCache() {
        log.debug("All links cache cleared");
    }

    @CacheEvict(value = "popular-links", allEntries = true)
    public void clearPopularLinksCache() {
        log.debug("Popular links cache cleared");
    }

    private LinkResponse mapToResponse(Link link) {
        LinkResponse response = new LinkResponse();
        response.setId(link.getId());
        response.setOriginalUrl(link.getOriginalUrl());
        response.setShortCode(link.getShortCode());
        response.setShortUrl("http://localhost:8080/r/" + link.getShortCode());
        response.setClickCount(link.getClickCount());
        response.setCreatedAt(link.getCreatedAt());
        return response;
    }
}
