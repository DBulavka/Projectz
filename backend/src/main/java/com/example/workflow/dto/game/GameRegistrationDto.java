package com.example.workflow.dto.game;

import com.example.workflow.enums.GameRegistrationStatus;

import java.util.UUID;

public record GameRegistrationDto(
        UUID id,
        UUID gameId,
        UUID groupId,
        GameRegistrationStatus status
) {
}
