package com.example.urlshortener.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
public class ShortenRequest {
    @NotBlank(message = "URL cannot be blank")
    @Pattern(regexp = "^https?://.+", message = "URL must start with http:// or https://")
    private String originalUrl;
    public String getOriginalUrl() { return originalUrl; }
    public void setOriginalUrl(String u) { this.originalUrl = u; }
}
