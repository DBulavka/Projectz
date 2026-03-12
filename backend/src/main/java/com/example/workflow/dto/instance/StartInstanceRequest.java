package com.example.workflow.dto.instance;

import java.util.UUID;
import java.util.Map;

public record StartInstanceRequest(UUID assigneeGroupId, Map<String, Object> variables) {}
