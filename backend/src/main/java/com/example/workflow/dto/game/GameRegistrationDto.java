package com.example.workflow.dto.game;

import com.example.workflow.enums.GameRegistrationStatus;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class GameRegistrationDto {
    UUID id;
    UUID gameId;
    UUID groupId;
    GameRegistrationStatus status;
}
