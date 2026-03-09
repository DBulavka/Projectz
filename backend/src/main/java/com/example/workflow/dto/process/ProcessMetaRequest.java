package com.example.workflow.dto.process;

import jakarta.validation.constraints.NotBlank;

public record ProcessMetaRequest(@NotBlank String key, @NotBlank String name, String description, String category) {}
