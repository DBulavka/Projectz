package com.example.workflow.dto.process;

public record GameLevelCodeItemDto(
        String value,
        String description,
        GameCodeDifficultyDto difficulty
) {}
