package com.example.workflow.dto.group;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
import java.util.UUID;

@Jacksonized
@Value
@Builder
public class GroupTelegramChatsDto {
    UUID groupId;
    List<String> chatIds;
}
