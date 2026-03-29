package com.example.workflow.dto.game;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;

@Jacksonized
@Value
@Builder
public class GameCreateRequest {
    @NotBlank String code;
    @NotBlank String processDefinitionId;
    @NotBlank String name;
    String description;
    @NotNull @Future Instant startAt;
}
