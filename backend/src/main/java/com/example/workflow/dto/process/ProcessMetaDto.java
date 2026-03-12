package com.example.workflow.dto.process;

import java.time.Instant;

public record ProcessMetaDto(
        String id,
        String ownerGroupId,
        String key,
        String name,
        String description,
        String category,
        Instant createdAt,
        Instant updatedAt
) {}
