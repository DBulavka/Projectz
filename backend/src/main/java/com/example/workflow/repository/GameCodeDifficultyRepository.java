package com.example.workflow.repository;

import com.example.workflow.entity.GameCodeDifficulty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GameCodeDifficultyRepository extends JpaRepository<GameCodeDifficulty, UUID> {
    Optional<GameCodeDifficulty> findByValue(String value);
}
