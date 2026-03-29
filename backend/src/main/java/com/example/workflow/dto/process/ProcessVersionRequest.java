package com.example.workflow.dto.process;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Value
@Builder
public class ProcessVersionRequest {
    @NotBlank String bpmnXml;
}
