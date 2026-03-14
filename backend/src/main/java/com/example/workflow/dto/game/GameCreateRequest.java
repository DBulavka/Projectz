package com.example.workflow.dto.game;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

@lombok.Value
@lombok.Builder
public class GameCreateRequest {
    private @NotNull Integer number;
    private @NotBlank String processDefinitionId;
    private @NotBlank String name;
    private String description;
    private @NotNull @Future Instant startAt;
}
