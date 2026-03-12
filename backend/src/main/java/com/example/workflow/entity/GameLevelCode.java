package com.example.workflow.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "game_level_code")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameLevelCode {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "process_definition_meta_id", nullable = false)
    private UUID processDefinitionMetaId;

    @Column(name = "level_key", nullable = false)
    private String levelKey;

    @Column(nullable = false)
    private String value;

    private String description;

    @Column(name = "difficulty_id", nullable = false)
    private UUID difficultyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "difficulty_id", referencedColumnName = "id", insertable = false, updatable = false)
    private GameCodeDifficulty difficulty;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
