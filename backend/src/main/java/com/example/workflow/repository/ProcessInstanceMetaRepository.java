package com.example.workflow.repository;

import com.example.workflow.entity.ProcessInstanceMeta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProcessInstanceMetaRepository extends JpaRepository<ProcessInstanceMeta, Long> {
    List<ProcessInstanceMeta> findByOwnerIdOrderByStartedAtDesc(Long ownerId);
    Optional<ProcessInstanceMeta> findByFlowableProcessInstanceId(String flowableProcessInstanceId);
}
