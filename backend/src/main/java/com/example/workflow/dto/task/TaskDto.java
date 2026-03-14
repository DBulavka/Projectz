package com.example.workflow.dto.task;

import java.time.Instant;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TaskDto {
    String id;
    String name;
    String assignee;
    String processInstanceId;
    Instant createTime;
    Instant dueDate;
    TaskGameProgressDto gameProgress;
}
