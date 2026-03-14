package com.example.workflow.dto.task;

import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TaskGameProgressDto {
    int totalCodes;
    int doneCodes;
    List<TaskGameCodeDto> codes;
}
