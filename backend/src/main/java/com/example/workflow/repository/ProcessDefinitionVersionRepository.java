package com.example.workflow.repository;

import com.example.workflow.entity.ProcessDefinitionVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProcessDefinitionVersionRepository extends JpaRepository<ProcessDefinitionVersion, Long> {
    List<ProcessDefinitionVersion> findByProcessDefinitionMetaIdOrderByVersionNumberDesc(Long processDefinitionMetaId);
    int countByProcessDefinitionMetaId(Long processDefinitionMetaId);
}
