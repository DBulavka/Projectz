package com.example.workflow.dto.task;

import jakarta.validation.constraints.NotBlank;

public record SubmitGameCodeRequest(
        @NotBlank String value
) {}
