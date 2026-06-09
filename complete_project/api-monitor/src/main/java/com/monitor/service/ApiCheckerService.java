package com.monitor.service;

import com.monitor.model.ApiEntry;
import com.monitor.model.ApiStatusResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class ApiCheckerService {

    @Value("${monitor.timeout:5}")
    private int timeoutSeconds;

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");

    /** Check all APIs in parallel — much faster than sequential. */
    public List<ApiStatusResult> checkAll(List<ApiEntry> entries) {
        List<CompletableFuture<ApiStatusResult>> futures = entries.stream()
                .map(e -> CompletableFuture.supplyAsync(() -> check(e)))
                .toList();
        return futures.stream().map(CompletableFuture::join).toList();
    }

    public ApiStatusResult check(ApiEntry entry) {
        ApiStatusResult result = new ApiStatusResult();
        result.setName(entry.getName());
        result.setUrl(entry.getUrl());
        result.setDescription(entry.getDescription());
        result.setCheckedAt(LocalDateTime.now().format(FMT));

        int ms = timeoutSeconds * 1000;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(ms);
        factory.setReadTimeout(ms);
        RestTemplate rt = new RestTemplate(factory);

        long start = System.currentTimeMillis();
        try {
            ResponseEntity<String> resp = rt.getForEntity(entry.getUrl(), String.class);
            result.setResponseTimeMs(System.currentTimeMillis() - start);
            result.setHttpStatusCode(resp.getStatusCode().value());

            if (resp.getStatusCode().is2xxSuccessful()) {
                result.setStatus("UP");
            } else {
                result.setStatus("DOWN");
                result.setErrorMessage("HTTP " + resp.getStatusCode().value());
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            result.setResponseTimeMs(System.currentTimeMillis() - start);
            result.setStatus("DOWN");
            result.setHttpStatusCode(e.getStatusCode().value());
            result.setErrorMessage("HTTP " + e.getStatusCode().value());

        } catch (Exception e) {
            result.setResponseTimeMs(System.currentTimeMillis() - start);
            result.setStatus("DOWN");
            result.setErrorMessage(simplify(e.getMessage()));
        }
        return result;
    }

    private String simplify(String msg) {
        if (msg == null) return "Connection failed";
        if (msg.contains("refused"))   return "Connection refused";
        if (msg.contains("timed out")) return "Request timed out";
        if (msg.contains("resolved"))  return "DNS resolution failed";
        return msg.length() > 80 ? msg.substring(0, 80) + "..." : msg;
    }
}
