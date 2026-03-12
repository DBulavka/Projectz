package com.example.workflow.dto.process;

import jakarta.validation.constraints.NotBlank;

public record GameLevelCodeItemRequest(
        @NotBlank String value,
        String description,
        @NotBlank String difficultyValue,
        String difficultyDescription
) {}
