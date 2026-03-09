package com.example.workflow.entity;

import com.example.workflow.enums.InstanceStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "process_instance_meta")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProcessInstanceMeta {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long processDefinitionMetaId;
    @Column(nullable = false)
    private Long processDefinitionVersionId;
    @Column(nullable = false)
    private Long ownerId;
    @Column(nullable = false, unique = true)
    private String flowableProcessInstanceId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InstanceStatus status;
    @Column(nullable = false)
    private Instant startedAt;
    private Instant endedAt;
}
