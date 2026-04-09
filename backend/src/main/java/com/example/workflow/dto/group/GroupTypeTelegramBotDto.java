package com.example.workflow.dto.group;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Jacksonized
@Value
@Builder
public class GroupTypeTelegramBotDto {
    UUID groupTypeId;
    boolean configured;
}
