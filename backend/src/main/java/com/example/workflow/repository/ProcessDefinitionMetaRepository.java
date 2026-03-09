package com.example.workflow.repository;

import com.example.workflow.entity.ProcessDefinitionMeta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProcessDefinitionMetaRepository extends JpaRepository<ProcessDefinitionMeta, Long> {
    List<ProcessDefinitionMeta> findByOwnerId(Long ownerId);
}
