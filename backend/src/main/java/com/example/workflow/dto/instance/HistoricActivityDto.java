package com.example.workflow.dto.instance;

import java.time.Instant;

public record HistoricActivityDto(
        String activityId,
        String activityName,
        String activityType,
        String assignee,
        Instant startTime,
        Instant endTime,
        Long durationInMillis
) {}
