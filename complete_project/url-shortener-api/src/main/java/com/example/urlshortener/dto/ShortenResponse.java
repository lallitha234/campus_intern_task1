package com.example.urlshortener.dto;
public class ShortenResponse {
    private String originalUrl, shortCode, shortUrl;
    private long clickCount;
    public ShortenResponse(String originalUrl, String shortCode, String shortUrl, long clickCount) {
        this.originalUrl = originalUrl; this.shortCode = shortCode;
        this.shortUrl = shortUrl;       this.clickCount = clickCount;
    }
    public String getOriginalUrl() { return originalUrl; }
    public String getShortCode()   { return shortCode; }
    public String getShortUrl()    { return shortUrl; }
    public long getClickCount()    { return clickCount; }
}
