package com.example.workflow.repository;

import com.example.workflow.entity.UserGroupMembership;
import com.example.workflow.enums.GroupRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserGroupMembershipRepository extends JpaRepository<UserGroupMembership, UUID> {
    List<UserGroupMembership> findByUserId(UUID userId);
    List<UserGroupMembership> findByUserIdAndGroupIdIn(UUID userId, Collection<UUID> groupIds);
    Optional<UserGroupMembership> findByUserIdAndGroupId(UUID userId, UUID groupId);
    boolean existsByUserIdAndGroupIdAndGroupRole(UUID userId, UUID groupId, GroupRole groupRole);
}
