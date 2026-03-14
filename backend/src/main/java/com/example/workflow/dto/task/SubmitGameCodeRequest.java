package com.example.workflow.dto.task;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SubmitGameCodeRequest {
    @NotBlank String value;
}
