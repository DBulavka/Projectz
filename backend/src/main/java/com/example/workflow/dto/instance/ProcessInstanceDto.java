package com.example.workflow.dto.instance;

import java.time.Instant;

@lombok.Value
@lombok.Builder
public class ProcessInstanceDto {
    private String id;
    private String processId;
    private String businessKey;
    private String businessStatus;
    private Instant startTime;
    private Instant endTime;
}
