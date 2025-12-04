package com.example.urlShortener.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class CreateLinkRequest {
    @NotBlank(message = "URL is required")
    @URL(message = "Must be a valid URL")
    private String originalUrl;
}
