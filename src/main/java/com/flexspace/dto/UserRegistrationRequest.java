package com.flexspace.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;

public class UserRegistrationRequest {

    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String name; // only for register

    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getName() { return name; }
}
