package com.example.workflow.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "audit_log")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuditLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    @Column(nullable = false)
    private String entityType;
    @Column(nullable = false)
    private String entityId;
    @Column(nullable = false)
    private String action;
    @Lob
    private String payloadJson;
    @Column(nullable = false)
    private Instant createdAt;
}
