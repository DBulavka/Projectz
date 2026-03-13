package com.example.workflow.dto.task;

import java.util.List;

public record TaskGameProgressDto(
        int totalCodes,
        int doneCodes,
        List<TaskGameCodeDto> codes
) {}
