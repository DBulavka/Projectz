package com.example.workflow.dto.process;

import com.example.workflow.enums.VersionStatus;

import java.time.Instant;

@lombok.Value
@lombok.Builder
public class ProcessVersionDto {
    private String id;
    private String processDefinitionMetaId;
    private Integer versionNumber;
    private VersionStatus status;
    private Instant createdAt;
    private Instant publishedAt;
}
