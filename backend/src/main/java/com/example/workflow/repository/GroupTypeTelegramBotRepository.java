package com.example.workflow.repository;

import com.example.workflow.entity.GroupTypeTelegramBot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupTypeTelegramBotRepository extends JpaRepository<GroupTypeTelegramBot, UUID> {
    Optional<GroupTypeTelegramBot> findByGroupTypeId(UUID groupTypeId);

    List<GroupTypeTelegramBot> findByGroupTypeIdIn(Collection<UUID> groupTypeIds);
}
