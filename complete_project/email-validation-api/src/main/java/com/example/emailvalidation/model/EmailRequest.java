package com.example.emailvalidation.model;
import jakarta.validation.constraints.NotBlank;
public class EmailRequest {
    @NotBlank(message = "Email cannot be blank")
    private String email;
    public String getEmail() { return email; }
    public void setEmail(String e) { this.email = e; }
}
