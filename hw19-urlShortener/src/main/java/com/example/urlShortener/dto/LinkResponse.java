package com.example.urlShortener.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class LinkResponse {
    private Long id;
    private String originalUrl;
    private String shortUrl;
    private String shortCode;
    private Integer clickCount;
    private LocalDateTime createdAt;
}
