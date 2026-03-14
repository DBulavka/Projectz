package com.example.workflow.dto.process;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class GameLevelCodesRequest {
    @NotEmpty List<@Valid GameLevelCodeItemRequest> codes;
}
