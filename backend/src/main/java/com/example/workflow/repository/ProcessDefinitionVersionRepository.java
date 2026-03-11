package com.example.workflow.repository;

import com.example.workflow.entity.ProcessDefinitionVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProcessDefinitionVersionRepository extends JpaRepository<ProcessDefinitionVersion, UUID> {
    List<ProcessDefinitionVersion> findByProcessDefinitionMetaIdOrderByVersionNumberDesc(UUID processDefinitionMetaId);
    int countByProcessDefinitionMetaId(UUID processDefinitionMetaId);
}
