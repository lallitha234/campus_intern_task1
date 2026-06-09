package com.monitor.service;

import com.monitor.model.ApiStatusResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class AiAnalysisService {

    @Value("${anthropic.api.key:}")
    private String configuredKey;

    /**
     * Calls Claude AI to analyze API health results.
     * Falls back to a rule-based summary if no API key is provided.
     */
    public String analyze(List<ApiStatusResult> results, String runtimeKey) {
        String key = (runtimeKey != null && !runtimeKey.isBlank())
                ? runtimeKey.trim()
                : configuredKey;

        if (key == null || key.isBlank()) {
            return ruleBased(results);
        }

        try {
            return callClaude(results, key);
        } catch (Exception e) {
            return ruleBased(results);
        }
    }

    // ─── Claude API call ────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private String callClaude(List<ApiStatusResult> results, String key) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are an API monitoring assistant. Analyze the following API health check results and provide:\n");
        prompt.append("1. A brief overall system health assessment (1-2 sentences)\n");
        prompt.append("2. Key observations about any DOWN APIs (if any)\n");
        prompt.append("3. Two or three actionable recommendations\n\n");
        prompt.append("Results:\n");

        for (ApiStatusResult r : results) {
            prompt.append(String.format("- %s (%s): %s | Response time: %dms",
                    r.getName(), r.getUrl(), r.getStatus(), r.getResponseTimeMs()));
            if (r.getErrorMessage() != null)
                prompt.append(" | Error: ").append(r.getErrorMessage());
            prompt.append("\n");
        }
        prompt.append("\nKeep the response concise and professional (max 150 words).");

        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", key);
        headers.set("anthropic-version", "2023-06-01");
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "model", "claude-haiku-4-5-20251001",
                "max_tokens", 400,
                "messages", List.of(Map.of("role", "user", "content", prompt.toString()))
        );

        ResponseEntity<Map> resp = rt.postForEntity(
                "https://api.anthropic.com/v1/messages",
                new HttpEntity<>(body, headers), Map.class
        );

        List<Map<String, Object>> content =
                (List<Map<String, Object>>) resp.getBody().get("content");
        return (String) content.get(0).get("text");
    }

    // ─── Rule-based fallback ─────────────────────────────────────────────────

    private String ruleBased(List<ApiStatusResult> results) {
        long up   = results.stream().filter(ApiStatusResult::isUp).count();
        long down = results.size() - up;
        double pct = results.isEmpty() ? 0 : (up * 100.0 / results.size());

        StringBuilder sb = new StringBuilder();
        sb.append(String.format(
                "System availability is %.1f%% (%d of %d APIs are operational).\n\n",
                pct, up, results.size()));

        if (down > 0) {
            sb.append("DOWN APIs:\n");
            results.stream().filter(r -> !r.isUp()).forEach(r ->
                    sb.append(String.format("  • %s — %s\n",
                            r.getName(),
                            r.getErrorMessage() != null ? r.getErrorMessage() : "Unreachable")));
            sb.append("\nRecommendations:\n");
            sb.append("  1. Verify server is running and the port is correct.\n");
            sb.append("  2. Check firewall rules and network connectivity.\n");
            sb.append("  3. Review application logs for errors or crashes.\n");
        } else {
            sb.append("All services are running normally.\n\n");
            sb.append("Recommendations:\n");
            sb.append("  1. Continue regular monitoring at scheduled intervals.\n");
            sb.append("  2. Set up automated alerts for any future downtime.\n");
            sb.append("  3. Monitor response times for performance degradation.\n");
        }

        if (!results.isEmpty()) {
            double avgMs = results.stream()
                    .mapToLong(ApiStatusResult::getResponseTimeMs).average().orElse(0);
            sb.append(String.format("\nAverage response time: %.0f ms", avgMs));
        }

        return sb.toString();
    }
}
