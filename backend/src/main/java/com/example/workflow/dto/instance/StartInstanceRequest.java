package com.example.workflow.dto.instance;

import java.util.Map;

@lombok.Value
@lombok.Builder
public class StartInstanceRequest {
    private String businessKey;
    private Map<String, Object> variables;
}
