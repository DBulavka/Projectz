package com.example.workflow.dto.task;

import jakarta.validation.constraints.NotBlank;

@lombok.Value
@lombok.Builder
public class SubmitGameCodeRequest {
    private @NotBlank String value;
}
