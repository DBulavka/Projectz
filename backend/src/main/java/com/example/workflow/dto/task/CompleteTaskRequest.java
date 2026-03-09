package com.example.workflow.dto.task;

import java.util.Map;

public record CompleteTaskRequest(Map<String, Object> variables) {}
