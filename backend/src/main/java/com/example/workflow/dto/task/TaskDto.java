package com.example.workflow.dto.task;

import java.util.Date;

public record TaskDto(
        String id,
        String name,
        String assignee,
        String processInstanceId,
        Date createTime,
        Date dueDate
) {}
