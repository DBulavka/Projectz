package com.example.workflow.dto.game;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class GameDto {
    UUID id;
    String code;
    String processDefinitionId;
    String name;
    String description;
    Instant startAt;
    Instant startedAt;
}
