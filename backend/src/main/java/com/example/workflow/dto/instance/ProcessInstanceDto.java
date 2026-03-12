package com.example.workflow.dto.instance;

import com.example.workflow.enums.InstanceStatus;

import java.time.Instant;

public record ProcessInstanceDto(
        String id,
        String processDefinitionMetaId,
        String processDefinitionVersionId,
        String flowableProcessInstanceId,
        InstanceStatus status,
        Instant startedAt,
        Instant endedAt
) {}
