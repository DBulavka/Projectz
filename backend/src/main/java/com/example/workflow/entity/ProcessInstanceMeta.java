package com.example.workflow.entity;

import com.example.workflow.enums.InstanceStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "process_instance_meta")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProcessInstanceMeta {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false)
    private UUID processDefinitionMetaId;
    @Column(nullable = false)
    private UUID processDefinitionVersionId;
    @Column(nullable = false)
    private UUID ownerId;
    @Column(nullable = false, unique = true)
    private String flowableProcessInstanceId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InstanceStatus status;
    @Column(nullable = false)
    private Instant startedAt;
    private Instant endedAt;
}
