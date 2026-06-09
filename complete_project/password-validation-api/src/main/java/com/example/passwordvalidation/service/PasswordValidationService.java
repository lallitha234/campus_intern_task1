package com.example.passwordvalidation.service;
import com.example.passwordvalidation.model.PasswordResponse;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class PasswordValidationService {
    public PasswordResponse validate(String password) {
        List<String> errors = new ArrayList<>();
        List<String> suggestions = new ArrayList<>();
        if (password == null || password.isEmpty()) {
            errors.add("Password is required");
            return new PasswordResponse(false, "WEAK", "Password is invalid", errors, suggestions);
        }
        if (password.length() < 8)                            errors.add("Minimum 8 characters required");
        if (!password.matches(".*[A-Z].*"))                   errors.add("At least one uppercase letter required");
        if (!password.matches(".*[a-z].*"))                   errors.add("At least one lowercase letter required");
        if (!password.matches(".*[0-9].*"))                   errors.add("At least one digit required");
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>?/].*"))
                                                               errors.add("At least one special character required");
        if (password.length() < 12)  suggestions.add("Use 12+ characters for stronger security");
        if (password.length() < 16)  suggestions.add("Consider using a passphrase for maximum security");
        int score = 5 - errors.size();
        String strength = score <= 1 ? "WEAK" : score <= 3 ? "MEDIUM" : "STRONG";
        boolean valid = errors.isEmpty();
        return new PasswordResponse(valid, strength,
            valid ? "Password is " + strength : "Password is invalid", errors, suggestions);
    }
}
