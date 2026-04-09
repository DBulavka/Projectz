package com.example.workflow.dto.group;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Jacksonized
@Value
@Builder
public class GroupTelegramChatsRequest {
    @NotEmpty
    List<@NotBlank String> chatIds;
}
