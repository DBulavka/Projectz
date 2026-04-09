package com.example.workflow.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "group_type_telegram_bot",
        uniqueConstraints = @UniqueConstraint(name = "uq_group_type_telegram_bot", columnNames = {"group_type_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupTypeTelegramBot {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "group_type_id", nullable = false)
    private UUID groupTypeId;

    @Column(name = "bot_token", nullable = false)
    private String botToken;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;
}
