package com.example.workflow.dto.process;

import jakarta.validation.constraints.NotBlank;

@lombok.Value
@lombok.Builder
public class GameLevelCodeItemRequest {
    private @NotBlank String value;
    private String description;
    private @NotBlank String difficultyValue;
    private String difficultyDescription;
}
