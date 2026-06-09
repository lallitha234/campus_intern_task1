package com.example.emailvalidation.controller;
import com.example.emailvalidation.model.EmailRequest;
import com.example.emailvalidation.model.EmailResponse;
import com.example.emailvalidation.service.EmailValidationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/email")
@CrossOrigin(origins = "*")
public class EmailController {
    @Autowired EmailValidationService service;

    @PostMapping("/validate")
    public ResponseEntity<EmailResponse> validate(@Valid @RequestBody EmailRequest req) {
        return ResponseEntity.ok(service.validate(req.getEmail()));
    }

    @GetMapping("/validate")
    public ResponseEntity<EmailResponse> validateParam(@RequestParam String email) {
        return ResponseEntity.ok(service.validate(email));
    }

    @PostMapping("/validate/bulk")
    public ResponseEntity<?> bulk(@RequestBody Map<String, List<String>> body) {
        List<String> emails = body.get("emails");
        if (emails == null) return ResponseEntity.badRequest().body(Map.of("error","emails list required"));
        List<EmailResponse> results = emails.stream().map(service::validate).toList();
        long valid = results.stream().filter(EmailResponse::isValid).count();
        return ResponseEntity.ok(Map.of("total",results.size(),"valid",valid,"invalid",results.size()-valid,"results",results));
    }

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(Map.of("status","UP","service","Email Validation API"));
    }
}
