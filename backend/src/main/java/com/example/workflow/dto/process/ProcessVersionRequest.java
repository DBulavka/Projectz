package com.example.workflow.dto.process;

import jakarta.validation.constraints.NotBlank;

@lombok.Value
@lombok.Builder
public class ProcessVersionRequest {
    private @NotBlank String bpmnXml;
}
