package com.example.workflow.dto.audit;

import java.time.Instant;

public record AuditLogDto(
        Long id,
        Long userId,
        String entityType,
        String entityId,
        String action,
        String payloadJson,
        Instant createdAt
) {}
