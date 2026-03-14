package com.example.workflow.dto.game;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record GameRegistrationRequest(
        @NotNull UUID groupId
) {
}
