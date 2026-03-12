package com.example.workflow.dto.process;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record GameLevelCodesRequest(
        @NotEmpty List<@Valid GameLevelCodeItemRequest> codes
) {}
