package com.example.workflow.dto.game;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record GameCreateRequest(
        @NotNull Integer number,
        @NotBlank String processDefinitionId,
        @NotBlank String name,
        String description,
        @NotNull @Future Instant startAt
) {
}
