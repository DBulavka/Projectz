package com.example.workflow.dto.task;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Value
@Builder
public class SubmitGameCodeRequest {
    @NotBlank String value;
}
