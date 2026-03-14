package com.example.workflow.dto.game;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@lombok.Value
@lombok.Builder
public class GameRegistrationRequest {
    private @NotNull UUID groupId;
}
