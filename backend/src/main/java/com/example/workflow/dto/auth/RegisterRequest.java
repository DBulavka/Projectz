package com.example.workflow.dto.auth;

import jakarta.validation.constraints.NotBlank;

@lombok.Value
@lombok.Builder
public class RegisterRequest {
    private @NotBlank String email;
    private @NotBlank String password;
}
