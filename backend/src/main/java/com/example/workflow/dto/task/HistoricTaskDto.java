package com.example.workflow.dto.task;

import java.util.Date;

@lombok.Value
@lombok.Builder
public class HistoricTaskDto {
    private String id;
    private String name;
    private String assignee;
    private String processInstanceId;
    private Date createTime;
    private Date endTime;
    private Long durationInMillis;
    private String deleteReason;
}
