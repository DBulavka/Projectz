package com.example.workflow.dto.instance;

import com.example.workflow.enums.InstanceStatus;

import java.time.Instant;
import java.util.UUID;

public record ProcessInstanceDto(
        UUID id,
        UUID processDefinitionMetaId,
        UUID processDefinitionVersionId,
        InstanceStatus status,
        Instant startedAt,
        Instant endedAt
) {}
