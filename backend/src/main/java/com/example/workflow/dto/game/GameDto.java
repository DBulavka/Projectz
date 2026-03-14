package com.example.workflow.dto.game;

import java.time.Instant;
import java.util.UUID;

public record GameDto(
        UUID id,
        Integer number,
        String processDefinitionId,
        String name,
        String description,
        Instant startAt,
        Instant startedAt
) {
}
