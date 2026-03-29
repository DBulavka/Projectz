package com.example.workflow.dto.task;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class TaskDto {
    String id;
    String name;
    String assignee;
    String processInstanceId;
    Instant createTime;
    Instant dueDate;
    String duration;
    TaskGameProgressDto gameProgress;
}
