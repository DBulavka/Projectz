package com.example.workflow.repository;

import com.example.workflow.entity.GameRegistration;
import com.example.workflow.enums.GameRegistrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GameRegistrationRepository extends JpaRepository<GameRegistration, UUID> {
    Optional<GameRegistration> findByGameIdAndGroupId(UUID gameId, UUID groupId);

    List<GameRegistration> findByGameId(UUID gameId);

    List<GameRegistration> findByGameIdAndStatus(UUID gameId, GameRegistrationStatus status);
}
