package com.example.workflow.dto.process;

import java.util.List;

public record GameLevelCodesDto(
        String levelKey,
        List<String> codes
) {}
