package com.example.workflow.dto.process;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

@lombok.Value
@lombok.Builder
public class ProcessMetaRequest {
    private @NotBlank String key;
    private @NotBlank String name;
    private String description;
    private String category;
    private UUID ownerGroupId;
}
