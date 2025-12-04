package com.example.urlShortener.service;

import com.example.urlShortener.annotation.MeasureTime;
import com.example.urlShortener.cache.LinkCache;
import com.example.urlShortener.dao.LinkRepository;
import com.example.urlShortener.dto.LinkResponse;
import com.example.urlShortener.entity.Link;
import com.example.urlShortener.util.CodeGenerator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LinkService {

    private final LinkRepository linkRepository;
    private final CodeGenerator codeGenerator;
    private final LinkCache linkCache;

    @MeasureTime("createShortLink")
    @Transactional
    public LinkResponse createShortLink(String originalUrl) {
        log.info("Creating short link for: {}", originalUrl);

        String shortCode = codeGenerator.generateUniqueCode();
        log.info("Generated unique code: {}", shortCode);

        Link link = new Link(originalUrl, shortCode);
        Link savedLink = linkRepository.save(link);
        log.info("Link saved with ID: {}", savedLink.getId());

        linkCache.put(shortCode, originalUrl);

        return mapToResponse(savedLink);
    }

    @MeasureTime("getOriginalUrl")
    public Optional<String> getOriginalUrl(String shortCode) {
        log.debug("Looking up URL for code: {}", shortCode);

        String cachedUrl = linkCache.get(shortCode);
        if (cachedUrl != null) {
            log.debug("Found in cache: {}", shortCode);
            return Optional.of(cachedUrl);
        }

        Optional<Link> linkOpt = linkRepository.findByShortCode(shortCode);
        if (linkOpt.isPresent()) {
            Link link = linkOpt.get();
            linkCache.put(shortCode, link.getOriginalUrl());
            log.debug("Found in DB and cached: {}", shortCode);
            return Optional.of(link.getOriginalUrl());
        }

        log.debug("Not found: {}", shortCode);
        return Optional.empty();
    }

    @Transactional
    public void incrementClickCount(String shortCode) {
        Optional<Link> linkOpt = linkRepository.findByShortCode(shortCode);
        if (linkOpt.isPresent()) {
            Link link = linkOpt.get();
            link.setClickCount(link.getClickCount() + 1);
            linkRepository.save(link);
            log.info("Click count updated for {}: {}", shortCode, link.getClickCount());
        }
    }

    public List<LinkResponse> getAllLinks() {
        return linkRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<LinkResponse> getTopPopular(int limit) {
        return linkRepository.findTopPopular(limit).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
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
