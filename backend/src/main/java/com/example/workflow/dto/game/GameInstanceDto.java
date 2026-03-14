package com.example.workflow.dto.game;

import com.example.workflow.enums.GameInstanceStatus;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class GameInstanceDto {
    UUID id;
    UUID gameId;
    UUID groupId;
    String processInstanceId;
    GameInstanceStatus status;
}
