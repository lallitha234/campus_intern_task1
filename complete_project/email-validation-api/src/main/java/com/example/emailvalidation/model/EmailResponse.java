package com.example.emailvalidation.model;
import java.util.List;
public class EmailResponse {
    private String email;
    private boolean valid;
    private String message;
    private List<String> errors;
    public EmailResponse(String email, boolean valid, String message, List<String> errors) {
        this.email = email; this.valid = valid; this.message = message; this.errors = errors;
    }
    public String getEmail()        { return email; }
    public boolean isValid()        { return valid; }
    public String getMessage()      { return message; }
    public List<String> getErrors() { return errors; }
}
