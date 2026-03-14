package com.example.workflow.dto.process;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class GameLevelCodeItemDto {
    String value;
    String description;
    GameCodeDifficultyDto difficulty;
}
