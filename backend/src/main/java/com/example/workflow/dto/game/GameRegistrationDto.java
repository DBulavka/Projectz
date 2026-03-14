package com.example.workflow.dto.game;

import com.example.workflow.enums.GameRegistrationStatus;

import java.util.UUID;

@lombok.Value
@lombok.Builder
public class GameRegistrationDto {
    private UUID id;
    private UUID gameId;
    private UUID groupId;
    private GameRegistrationStatus status;
}
