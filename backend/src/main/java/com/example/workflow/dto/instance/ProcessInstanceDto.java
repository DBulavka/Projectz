package com.example.workflow.dto.instance;

import java.time.Instant;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ProcessInstanceDto {
    String id;
    String processId;
    String businessKey;
    String businessStatus;
    Instant startTime;
    Instant endTime;
}
