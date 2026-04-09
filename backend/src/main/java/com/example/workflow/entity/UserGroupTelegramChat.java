package com.example.workflow.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_group_telegram_chat",
        uniqueConstraints = @UniqueConstraint(name = "uq_group_telegram_chat", columnNames = {"group_id", "chat_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserGroupTelegramChat {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "group_id", nullable = false)
    private UUID groupId;

    @Column(name = "chat_id", nullable = false)
    private String chatId;

    @Column(nullable = false)
    private Instant createdAt;
}
