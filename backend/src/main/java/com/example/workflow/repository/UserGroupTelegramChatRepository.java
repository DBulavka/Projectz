package com.example.workflow.repository;

import com.example.workflow.entity.UserGroupTelegramChat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface UserGroupTelegramChatRepository extends JpaRepository<UserGroupTelegramChat, UUID> {
    List<UserGroupTelegramChat> findByGroupId(UUID groupId);

    List<UserGroupTelegramChat> findByGroupIdIn(Collection<UUID> groupIds);

    void deleteByGroupId(UUID groupId);
}
