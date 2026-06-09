package com.example.emailvalidation.service;
import com.example.emailvalidation.model.EmailResponse;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class EmailValidationService {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,63}$");

    public EmailResponse validate(String email) {
        List<String> errors = new ArrayList<>();
        if (email == null || email.isBlank()) {
            errors.add("Email cannot be empty");
            return new EmailResponse(email, false, "Email is invalid", errors);
        }
        String trimmed = email.trim().toLowerCase();
        if (!trimmed.contains("@"))   errors.add("Missing @ symbol");
        if (trimmed.contains(".."))   errors.add("Contains consecutive dots");
        if (!EMAIL_PATTERN.matcher(trimmed).matches() && errors.isEmpty())
            errors.add("Invalid email format");
        boolean valid = errors.isEmpty();
        return new EmailResponse(trimmed, valid,
            valid ? "Email '" + trimmed + "' is valid" : "Email '" + trimmed + "' is invalid",
            errors);
    }
}
