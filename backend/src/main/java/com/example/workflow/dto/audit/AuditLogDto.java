package com.example.workflow.dto.audit;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AuditLogDto {
    UUID id;
    UUID userId;
    String entityType;
    String entityId;
    String action;
    String payloadJson;
    Instant createdAt;
}
