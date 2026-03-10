package com.example.workflow.dto.process;

import com.example.workflow.enums.VersionStatus;

import java.time.Instant;

public record ProcessVersionDto(
        Long id,
        Long processDefinitionMetaId,
        Integer versionNumber,
        VersionStatus status,
        Instant createdAt,
        Instant publishedAt
) {}
