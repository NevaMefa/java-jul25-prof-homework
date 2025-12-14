package com.example.urlShortener.controller;

import com.example.urlShortener.dto.ApiResponse;
import com.example.urlShortener.dto.CreateLinkRequest;
import com.example.urlShortener.dto.LinkResponse;
import com.example.urlShortener.service.LinkService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LinkController {

    private final LinkService linkService;

    @PostMapping("/links")
    public ResponseEntity<ApiResponse<LinkResponse>> createShortLink(@RequestBody @Valid CreateLinkRequest request) {

        LinkResponse linkResponse = linkService.createShortLink(request.getOriginalUrl());
        return ResponseEntity.ok(ApiResponse.success("Short link created", linkResponse));
    }

    @GetMapping("/links")
    public ResponseEntity<ApiResponse<List<LinkResponse>>> getAllLinks() {
        List<LinkResponse> links = linkService.getAllLinks();
        return ResponseEntity.ok(ApiResponse.success(links));
    }

    @GetMapping("/links/popular")
    public ResponseEntity<ApiResponse<List<LinkResponse>>> getPopularLinks(
            @RequestParam(defaultValue = "10") int limit) {

        List<LinkResponse> popularLinks = linkService.getTopPopular(limit);
        return ResponseEntity.ok(ApiResponse.success(popularLinks));
    }
}
