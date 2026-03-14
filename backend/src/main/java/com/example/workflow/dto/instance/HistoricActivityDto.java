package com.example.workflow.dto.instance;

import java.time.Instant;

@lombok.Value
@lombok.Builder
public class HistoricActivityDto {
    private String activityId;
    private String activityName;
    private String activityType;
    private String assignee;
    private Instant startTime;
    private Instant endTime;
    private Long durationInMillis;
}
