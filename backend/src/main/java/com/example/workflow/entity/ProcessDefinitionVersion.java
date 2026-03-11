package com.example.workflow.entity;

import com.example.workflow.enums.VersionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "process_definition_version")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProcessDefinitionVersion {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false)
    private UUID processDefinitionMetaId;
    @Column(nullable = false)
    private Integer versionNumber;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String bpmnXml;
    private String flowableDeploymentId;
    private String flowableProcessDefinitionKey;
    private String flowableProcessDefinitionId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VersionStatus status;
    @Column(nullable = false)
    private Instant createdAt;
    private Instant publishedAt;
}
