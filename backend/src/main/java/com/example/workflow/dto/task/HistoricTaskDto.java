package com.example.workflow.dto.task;

import java.util.Date;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class HistoricTaskDto {
    String id;
    String name;
    String assignee;
    String processInstanceId;
    Date createTime;
    Date endTime;
    Long durationInMillis;
    String deleteReason;
}
