package com.example.workflow.dto.task;

import java.util.Date;

public record HistoricTaskDto(
        String id,
        String name,
        String assignee,
        String processInstanceId,
        Date createTime,
        Date endTime,
        Long durationInMillis,
        String deleteReason
) {}
