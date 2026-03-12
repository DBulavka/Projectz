package com.example.workflow.repository;

import com.example.workflow.entity.GameCodeAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GameCodeAttemptRepository extends JpaRepository<GameCodeAttempt, UUID> {
    List<GameCodeAttempt> findByTaskIdOrderByCreatedAtAsc(String taskId);
}
