package com.example.workflow.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "game_code_attempt")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameCodeAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "task_id", nullable = false)
    private String taskId;

    @Column(name = "process_id", nullable = false)
    private String processId;

    @Column(name = "level_key", nullable = false)
    private String levelKey;

    @Column(name = "value", nullable = false)
    private String value;

    @Column(name = "is_correct", nullable = false)
    private boolean correct;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
