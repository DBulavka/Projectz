package com.example.workflow.dto.process;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Value
@Builder
public class GameLevelCodeItemRequest {
    @NotBlank String value;
    String description;
    @NotBlank String difficultyValue;
    String difficultyDescription;
}
