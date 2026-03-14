package com.example.workflow.dto.process;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@lombok.Value
@lombok.Builder
public class GameLevelCodesRequest {
    private @NotEmpty List<@Valid GameLevelCodeItemRequest> codes;
}
