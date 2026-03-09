package com.example.workflow.dto.process;

import jakarta.validation.constraints.NotBlank;

public record ProcessVersionRequest(@NotBlank String bpmnXml) {}
