package com.example.workflow.dto.process;

import com.example.workflow.enums.VersionStatus;

import java.time.Instant;

public record ProcessVersionDto(
        String id,
        String processDefinitionMetaId,
        Integer versionNumber,
        VersionStatus status,
        Instant createdAt,
        Instant publishedAt
) {}
