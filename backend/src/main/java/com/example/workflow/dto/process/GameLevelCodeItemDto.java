package com.example.workflow.dto.process;

@lombok.Value
@lombok.Builder
public class GameLevelCodeItemDto {
    private String value;
    private String description;
    private GameCodeDifficultyDto difficulty;
}
