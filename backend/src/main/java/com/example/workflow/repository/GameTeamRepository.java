package com.example.workflow.repository;

import com.example.workflow.entity.GameTeam;
import com.example.workflow.enums.GameTeamStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GameTeamRepository extends JpaRepository<GameTeam, UUID> {
    Optional<GameTeam> findByGameIdAndGroupId(UUID gameId, UUID groupId);

    List<GameTeam> findByGameId(UUID gameId);

    List<GameTeam> findByStatusIn(Collection<GameTeamStatus> statuses);
}
