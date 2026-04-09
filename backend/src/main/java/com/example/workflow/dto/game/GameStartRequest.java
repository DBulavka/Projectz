package com.example.workflow.dto.game;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.Map;
import java.util.UUID;

@Jacksonized
@Value
@Builder
public class GameStartRequest {
    UUID gameId;
    UUID groupId;
    Map<String, Object> variables;
}
