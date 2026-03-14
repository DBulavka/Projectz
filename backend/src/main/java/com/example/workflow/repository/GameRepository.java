package com.example.workflow.repository;

import com.example.workflow.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GameRepository extends JpaRepository<Game, UUID> {
    Optional<Game> findByCode(String code);

    List<Game> findByStartedAtIsNullAndStartAtLessThanEqual(Instant now);
}
