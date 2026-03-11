package com.example.workflow.util;

import com.example.workflow.entity.UserGroupMembership;
import com.example.workflow.enums.GroupRole;
import com.example.workflow.exception.ApiException;
import com.example.workflow.repository.UserGroupMembershipRepository;
import com.example.workflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SecurityUtils {
    private final UserRepository userRepository;
    private final UserGroupMembershipRepository membershipRepository;

    public String currentEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) throw new ApiException("Unauthenticated");
        return auth.getName();
    }

    public UUID currentUserId() {
        return userRepository.findByEmail(currentEmail()).orElseThrow(() -> new ApiException("User not found")).getId();
    }

    public boolean isAdmin() {
        return userRepository.findByEmail(currentEmail()).orElseThrow(() -> new ApiException("User not found")).getRole().name().equals("ADMIN");
    }

    public Set<UUID> currentUserGroupIds() {
        if (isAdmin()) return Set.of();
        return membershipRepository.findByUserId(currentUserId()).stream()
                .map(UserGroupMembership::getGroupId)
                .collect(Collectors.toSet());
    }

    public boolean hasGroupAccess(UUID groupId) {
        if (isAdmin()) return true;
        return membershipRepository.findByUserIdAndGroupId(currentUserId(), groupId).isPresent();
    }

    public boolean canManageGroup(UUID groupId) {
        if (isAdmin()) return true;
        return membershipRepository.existsByUserIdAndGroupIdAndGroupRole(currentUserId(), groupId, GroupRole.EDITOR);
    }

    public List<UserGroupMembership> currentUserMembershipsForGroups(Set<UUID> groupIds) {
        if (isAdmin() || groupIds.isEmpty()) return List.of();
        return membershipRepository.findByUserIdAndGroupIdIn(currentUserId(), groupIds);
    }
}
