package com.example.workflow.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_log")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuditLog {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID userId;
    @Column(nullable = false)
    private String entityType;
    @Column(nullable = false)
    private String entityId;
    @Column(nullable = false)
    private String action;
    @Column(columnDefinition = "TEXT")
    private String payloadJson;
    @Column(nullable = false)
    private Instant createdAt;
}
