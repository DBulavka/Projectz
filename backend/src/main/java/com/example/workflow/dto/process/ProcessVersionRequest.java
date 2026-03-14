package com.example.workflow.dto.process;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ProcessVersionRequest {
    @NotBlank String bpmnXml;
}
