package com.example.workflow.dto.game;

import com.example.workflow.enums.GameInstanceStatus;

import java.util.UUID;

public record GameInstanceDto(
        UUID id,
        UUID gameId,
        UUID groupId,
        String processInstanceId,
        GameInstanceStatus status
) {
}
