package com.example.workflow.repository;

import com.example.workflow.entity.GameLevelCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GameLevelCodeRepository extends JpaRepository<GameLevelCode, UUID> {
    List<GameLevelCode> findByProcessDefinitionMetaIdAndLevelKey(UUID processDefinitionMetaId, String levelKey);
}
