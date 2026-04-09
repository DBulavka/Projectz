package com.example.workflow.repository;

import com.example.workflow.entity.GameInstance;
import com.example.workflow.enums.GameInstanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GameInstanceRepository extends JpaRepository<GameInstance, UUID> {
    Optional<GameInstance> findByGameIdAndGroupId(UUID gameId, UUID groupId);

    List<GameInstance> findByGameId(UUID gameId);

    List<GameInstance> findByStatusIn(Collection<GameInstanceStatus> statuses);

    Optional<GameInstance> findByProcessInstanceId(String processInstanceId);
}
