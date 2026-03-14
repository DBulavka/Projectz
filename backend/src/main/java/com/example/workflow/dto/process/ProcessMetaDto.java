package com.example.workflow.dto.process;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ProcessMetaDto {
    String id;
    String name;
    String description;
    int version;
    String deploymentId;
}
