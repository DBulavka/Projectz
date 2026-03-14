package com.example.workflow.dto.audit;

import java.time.Instant;
import java.util.UUID;

@lombok.Value
@lombok.Builder
public class AuditLogDto {
    private UUID id;
    private UUID userId;
    private String entityType;
    private String entityId;
    private String action;
    private String payloadJson;
    private Instant createdAt;
}
