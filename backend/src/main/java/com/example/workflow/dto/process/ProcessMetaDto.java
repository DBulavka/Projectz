package com.example.workflow.dto.process;

public record ProcessMetaDto(
        String id,
        String name,
        String description,
        int version,
        String deploymentId
) {}
