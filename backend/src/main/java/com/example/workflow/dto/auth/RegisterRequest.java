package com.example.workflow.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(@NotBlank String email, @NotBlank String password) {}
