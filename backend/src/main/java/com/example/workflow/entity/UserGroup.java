package com.example.workflow.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_group")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserGroup {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID groupTypeId;

    @Column(nullable = false, unique = true)
    private String key;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;
}
