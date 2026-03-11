package com.example.workflow.dto.audit;

import java.time.Instant;
import java.util.UUID;

public record AuditLogDto(
        UUID id,
        UUID userId,
        String entityType,
        String entityId,
        String action,
        String payloadJson,
        Instant createdAt
) {}
