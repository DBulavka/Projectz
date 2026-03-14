package com.example.workflow.dto.task;

import java.util.List;

@lombok.Value
@lombok.Builder
public class TaskGameProgressDto {
    private int totalCodes;
    private int doneCodes;
    private List<TaskGameCodeDto> codes;
}
