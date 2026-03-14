package com.example.workflow.dto.process;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class ProcessMetaRequest {
    @NotBlank String key;
    @NotBlank String name;
    String description;
    String category;
    UUID ownerGroupId;
}
