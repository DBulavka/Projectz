package com.example.workflow.dto.instance;

import java.util.Map;

public record StartInstanceRequest(String businessKey, Map<String, Object> variables) {}
