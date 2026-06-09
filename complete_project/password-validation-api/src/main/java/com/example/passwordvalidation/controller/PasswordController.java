package com.example.passwordvalidation.controller;
import com.example.passwordvalidation.model.PasswordRequest;
import com.example.passwordvalidation.model.PasswordResponse;
import com.example.passwordvalidation.service.PasswordValidationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/password")
@CrossOrigin(origins = "*")
public class PasswordController {
    @Autowired PasswordValidationService service;

    @PostMapping("/validate")
    public ResponseEntity<PasswordResponse> validate(@Valid @RequestBody PasswordRequest req) {
        return ResponseEntity.ok(service.validate(req.getPassword()));
    }

    @GetMapping("/validate")
    public ResponseEntity<PasswordResponse> validateParam(@RequestParam String password) {
        return ResponseEntity.ok(service.validate(password));
    }

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(Map.of("status","UP","service","Password Validation API"));
    }
}
