package com.example.workflow.dto.instance;

import com.example.workflow.enums.InstanceStatus;

import java.time.Instant;

public record ProcessInstanceDto(
        Long id,
        Long processDefinitionMetaId,
        Long processDefinitionVersionId,
        InstanceStatus status,
        Instant startedAt,
        Instant endedAt
) {}
