package com.example.workflow.dto.process;

import com.example.workflow.enums.VersionStatus;

import java.time.Instant;
import java.util.UUID;

public record ProcessVersionDto(
        UUID id,
        UUID processDefinitionMetaId,
        Integer versionNumber,
        VersionStatus status,
        Instant createdAt,
        Instant publishedAt
) {}
