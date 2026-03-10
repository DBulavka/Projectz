package com.example.workflow.dto.process;

import java.time.Instant;

public record ProcessMetaDto(
        Long id,
        String key,
        String name,
        String description,
        String category,
        Instant createdAt,
        Instant updatedAt
) {}
