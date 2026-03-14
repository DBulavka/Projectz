package com.example.workflow.dto.game;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class GameRegistrationRequest {
    @NotNull UUID groupId;
}
