package com.example.workflow.dto.process;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class GameCodeDifficultyDto {
    String value;
    String description;
}
