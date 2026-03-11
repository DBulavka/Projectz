package com.example.workflow.dto.process;

import java.time.Instant;
import java.util.UUID;

public record ProcessMetaDto(
        UUID id,
        UUID ownerGroupId,
        String key,
        String name,
        String description,
        String category,
        Instant createdAt,
        Instant updatedAt
) {}
