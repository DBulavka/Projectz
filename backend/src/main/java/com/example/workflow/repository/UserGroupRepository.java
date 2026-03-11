package com.example.workflow.repository;

import com.example.workflow.entity.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface UserGroupRepository extends JpaRepository<UserGroup, UUID> {
    List<UserGroup> findByIdIn(Collection<UUID> ids);
}
