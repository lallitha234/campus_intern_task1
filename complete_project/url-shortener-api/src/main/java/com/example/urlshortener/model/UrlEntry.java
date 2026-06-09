package com.example.urlshortener.model;
import java.time.LocalDateTime;
public class UrlEntry {
    private String originalUrl;
    private String shortCode;
    private long clickCount;
    private LocalDateTime createdAt;
    public UrlEntry(String originalUrl, String shortCode) {
        this.originalUrl = originalUrl;
        this.shortCode   = shortCode;
        this.clickCount  = 0;
        this.createdAt   = LocalDateTime.now();
    }
    public String getOriginalUrl()  { return originalUrl; }
    public String getShortCode()    { return shortCode; }
    public long getClickCount()     { return clickCount; }
    public void incrementClick()    { this.clickCount++; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
