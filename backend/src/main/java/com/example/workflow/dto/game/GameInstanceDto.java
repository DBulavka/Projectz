package com.example.workflow.dto.game;

import com.example.workflow.enums.GameInstanceStatus;

import java.util.UUID;

@lombok.Value
@lombok.Builder
public class GameInstanceDto {
    private UUID id;
    private UUID gameId;
    private UUID groupId;
    private String processInstanceId;
    private GameInstanceStatus status;
}
