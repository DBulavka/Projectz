package com.example.workflow.dto.game;

import com.example.workflow.enums.GameTeamStatus;

import java.util.UUID;

public record GameTeamDto(
        UUID id,
        UUID gameId,
        UUID groupId,
        String processInstanceId,
        GameTeamStatus status
) {
}
