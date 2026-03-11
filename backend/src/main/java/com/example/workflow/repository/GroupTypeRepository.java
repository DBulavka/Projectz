package com.example.workflow.repository;

import com.example.workflow.entity.GroupType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GroupTypeRepository extends JpaRepository<GroupType, UUID> {
    Optional<GroupType> findByCode(String code);
}
