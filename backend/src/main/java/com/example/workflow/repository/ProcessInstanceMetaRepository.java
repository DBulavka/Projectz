package com.example.workflow.repository;

import com.example.workflow.entity.ProcessInstanceMeta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProcessInstanceMetaRepository extends JpaRepository<ProcessInstanceMeta, UUID> {
    List<ProcessInstanceMeta> findByOwnerIdOrderByStartedAtDesc(UUID ownerId);
    Optional<ProcessInstanceMeta> findByFlowableProcessInstanceId(String flowableProcessInstanceId);
}
