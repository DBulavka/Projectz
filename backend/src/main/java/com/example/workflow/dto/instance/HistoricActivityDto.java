package com.example.workflow.dto.instance;

import java.time.Instant;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class HistoricActivityDto {
    String activityId;
    String activityName;
    String activityType;
    String assignee;
    Instant startTime;
    Instant endTime;
    Long durationInMillis;
}
