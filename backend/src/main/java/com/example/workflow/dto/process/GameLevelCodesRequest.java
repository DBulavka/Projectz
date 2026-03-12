package com.example.workflow.dto.process;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record GameLevelCodesRequest(
        @NotEmpty List<String> codes
) {}
