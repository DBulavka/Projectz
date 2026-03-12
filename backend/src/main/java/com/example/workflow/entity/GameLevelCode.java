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

    @Column(nullable = false)
    private UUID processDefinitionMetaId;

    @Column(nullable = false)
    private String levelKey;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private Instant createdAt;
}
