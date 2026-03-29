package com.example.workflow.dto.instance;

import java.util.Map;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Value
@Builder
public class StartInstanceRequest {
    String businessKey;
    Map<String, Object> variables;
}
