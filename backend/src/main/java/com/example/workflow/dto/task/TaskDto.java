package com.example.workflow.dto.task;

import java.time.Instant;

public record TaskDto(
        String id,
        String name,
        String assignee,
        String processInstanceId,
        Instant createTime,
        Instant dueDate,
        TaskGameProgressDto gameProgress
) {}
