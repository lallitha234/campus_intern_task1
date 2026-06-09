package com.example.urlshortener.controller;
import com.example.urlshortener.dto.ShortenRequest;
import com.example.urlshortener.dto.ShortenResponse;
import com.example.urlshortener.service.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
public class UrlController {
    @Autowired UrlService urlService;

    @PostMapping("/api/shorten")
    public ResponseEntity<ShortenResponse> shorten(@Valid @RequestBody ShortenRequest req, HttpServletRequest http) {
        return ResponseEntity.status(HttpStatus.CREATED).body(urlService.shorten(req, baseUrl(http)));
    }

    @GetMapping("/{code}")
    public ResponseEntity<Void> redirect(@PathVariable String code) {
        return ResponseEntity.status(HttpStatus.FOUND).header("Location", urlService.resolve(code)).build();
    }

    @GetMapping("/api/info/{code}")
    public ResponseEntity<ShortenResponse> info(@PathVariable String code, HttpServletRequest http) {
        return ResponseEntity.ok(urlService.getInfo(code, baseUrl(http)));
    }

    @GetMapping("/api/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(java.util.Map.of("status","UP","service","URL Shortener API"));
    }

    private String baseUrl(HttpServletRequest r) {
        return r.getScheme() + "://" + r.getServerName() + ":" + r.getServerPort();
    }
}
