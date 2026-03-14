package com.example.workflow.dto.task;

import java.util.Map;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CompleteTaskRequest {
    Map<String, Object> variables;
    String code;
}
