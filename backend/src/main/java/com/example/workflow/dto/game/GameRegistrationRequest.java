package com.example.workflow.dto.game;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Jacksonized
@Value
@Builder
public class GameRegistrationRequest {
    @NotNull UUID groupId;
}
