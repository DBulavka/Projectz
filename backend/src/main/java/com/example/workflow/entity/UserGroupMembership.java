package com.example.workflow.entity;

import com.example.workflow.enums.GroupRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_group_membership", uniqueConstraints = @UniqueConstraint(name = "uq_group_membership", columnNames = {"user_id", "group_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserGroupMembership {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private UUID groupId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GroupRole groupRole;

    @Column(nullable = false)
    private Instant createdAt;
}
