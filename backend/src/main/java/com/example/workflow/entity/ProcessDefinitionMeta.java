package com.example.workflow.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "process_definition_meta")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProcessDefinitionMeta {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false)
    private UUID ownerId;
    @Column(nullable = false, unique = true)
    private String key;
    @Column(nullable = false)
    private String name;
    private String description;
    private String category;
    @Column(nullable = false)
    private Instant createdAt;
    @Column(nullable = false)
    private Instant updatedAt;
}
