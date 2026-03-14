package com.example.workflow.dto.task;

import java.util.Map;

@lombok.Value
@lombok.Builder
public class CompleteTaskRequest {
    private Map<String, Object> variables;
    private String code;
}
