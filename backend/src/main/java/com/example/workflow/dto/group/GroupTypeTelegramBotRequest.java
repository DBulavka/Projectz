package com.example.workflow.dto.group;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Value
@Builder
public class GroupTypeTelegramBotRequest {
    @NotBlank
    String botToken;
}
