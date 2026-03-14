package com.example.workflow.dto.process;

import com.example.workflow.enums.VersionStatus;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class ProcessVersionDto {
    String id;
    String processDefinitionMetaId;
    Integer versionNumber;
    VersionStatus status;
    Instant createdAt;
    Instant publishedAt;
}
