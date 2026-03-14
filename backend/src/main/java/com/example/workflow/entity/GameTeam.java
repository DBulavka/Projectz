package com.example.workflow.entity;

import com.example.workflow.enums.GameTeamStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "game_team",
        uniqueConstraints = @UniqueConstraint(name = "uq_game_team", columnNames = {"game_id", "group_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameTeam {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "game_id", nullable = false)
    private UUID gameId;

    @Column(name = "group_id", nullable = false)
    private UUID groupId;

    @Column(name = "process_instance_id")
    private String processInstanceId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameTeamStatus status;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;
}
