package com.example.workflow.dto.task;

import java.util.List;

public record SubmitGameCodeResponse(
        boolean levelCompleted,
        List<String> enteredCodes,
        List<String> requiredCodes
) {}
