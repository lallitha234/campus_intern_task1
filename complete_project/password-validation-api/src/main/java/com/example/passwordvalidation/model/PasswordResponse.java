package com.example.passwordvalidation.model;
import java.util.List;
public class PasswordResponse {
    private boolean valid;
    private String strength;
    private String message;
    private List<String> errors;
    private List<String> suggestions;
    public PasswordResponse(boolean valid, String strength, String message,
                            List<String> errors, List<String> suggestions) {
        this.valid = valid; this.strength = strength; this.message = message;
        this.errors = errors; this.suggestions = suggestions;
    }
    public boolean isValid()              { return valid; }
    public String getStrength()           { return strength; }
    public String getMessage()            { return message; }
    public List<String> getErrors()       { return errors; }
    public List<String> getSuggestions()  { return suggestions; }
}
