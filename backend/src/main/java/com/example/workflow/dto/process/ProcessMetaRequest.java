package com.example.workflow.dto.process;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record ProcessMetaRequest(
        @NotBlank String key,
        @NotBlank String name,
        String description,
        String category,
        UUID ownerGroupId
) {}
