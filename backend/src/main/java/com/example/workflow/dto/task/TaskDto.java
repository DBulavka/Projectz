package com.example.workflow.dto.task;

import java.time.Instant;

@lombok.Value
@lombok.Builder
public class TaskDto {
    private String id;
    private String name;
    private String assignee;
    private String processInstanceId;
    private Instant createTime;
    private Instant dueDate;
    private TaskGameProgressDto gameProgress;
}
