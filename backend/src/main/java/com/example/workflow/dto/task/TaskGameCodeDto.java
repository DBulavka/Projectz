package com.example.workflow.dto.task;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TaskGameCodeDto {
        String code;
        String difficulty;
        String value;
        String description;
        boolean done;
}
