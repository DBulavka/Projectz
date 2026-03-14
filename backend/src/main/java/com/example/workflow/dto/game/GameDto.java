package com.example.workflow.dto.game;

import java.time.Instant;
import java.util.UUID;

@lombok.Value
@lombok.Builder
public class GameDto {
    private UUID id;
    private Integer number;
    private String processDefinitionId;
    private String name;
    private String description;
    private Instant startAt;
    private Instant startedAt;
}
