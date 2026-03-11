package com.example.workflow.repository;

import com.example.workflow.entity.ProcessDefinitionMeta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface ProcessDefinitionMetaRepository extends JpaRepository<ProcessDefinitionMeta, UUID> {
    List<ProcessDefinitionMeta> findByOwnerGroupIdIn(Collection<UUID> ownerGroupIds);
}
