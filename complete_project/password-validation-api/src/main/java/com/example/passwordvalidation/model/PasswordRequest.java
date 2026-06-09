package com.example.passwordvalidation.model;
import jakarta.validation.constraints.NotBlank;
public class PasswordRequest {
    @NotBlank(message = "Password cannot be blank")
    private String password;
    public String getPassword() { return password; }
    public void setPassword(String p) { this.password = p; }
}
