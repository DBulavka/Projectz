package com.example.workflow.service;

import com.example.workflow.entity.AuditLog;
import com.example.workflow.repository.AuditLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuditService {
    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    public void log(Long userId, String entityType, String entityId, String action, Object payload) {
        String json = null;
        try { if (payload != null) json = objectMapper.writeValueAsString(payload); } catch (Exception ignored) {}
        auditLogRepository.save(AuditLog.builder()
                .userId(userId)
                .entityType(entityType)
                .entityId(entityId)
                .action(action)
                .payloadJson(json)
                .createdAt(Instant.now())
                .build());
    }
}
