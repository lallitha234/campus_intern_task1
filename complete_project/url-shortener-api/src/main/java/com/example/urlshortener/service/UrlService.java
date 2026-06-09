package com.example.urlshortener.service;
import com.example.urlshortener.dto.ShortenRequest;
import com.example.urlshortener.dto.ShortenResponse;
import com.example.urlshortener.model.UrlEntry;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UrlService {
    private final Map<String, UrlEntry> store = new ConcurrentHashMap<>();
    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private final SecureRandom random = new SecureRandom();

    public ShortenResponse shorten(ShortenRequest req, String baseUrl) {
        for (UrlEntry e : store.values())
            if (e.getOriginalUrl().equals(req.getOriginalUrl()))
                return toResponse(e, baseUrl);
        String code = generateCode();
        UrlEntry entry = new UrlEntry(req.getOriginalUrl(), code);
        store.put(code, entry);
        return toResponse(entry, baseUrl);
    }

    public String resolve(String code) {
        UrlEntry e = store.get(code);
        if (e == null) throw new RuntimeException("Short code not found: " + code);
        e.incrementClick();
        return e.getOriginalUrl();
    }

    public ShortenResponse getInfo(String code, String baseUrl) {
        UrlEntry e = store.get(code);
        if (e == null) throw new RuntimeException("Short code not found: " + code);
        return toResponse(e, baseUrl);
    }

    private String generateCode() {
        StringBuilder sb = new StringBuilder(6);
        do { sb.setLength(0);
            for (int i = 0; i < 6; i++) sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        } while (store.containsKey(sb.toString()));
        return sb.toString();
    }

    private ShortenResponse toResponse(UrlEntry e, String baseUrl) {
        return new ShortenResponse(e.getOriginalUrl(), e.getShortCode(),
                baseUrl + "/" + e.getShortCode(), e.getClickCount());
    }
}
