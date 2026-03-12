package com.example.workflow.dto.instance;

import java.time.Instant;

public record ProcessInstanceDto(
        String id,
        String processId,
        String businessKey,
        String businessStatus,
        Instant startTime,
        Instant endTime
) {}
