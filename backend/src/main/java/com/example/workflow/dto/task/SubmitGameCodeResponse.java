package com.example.workflow.dto.task;

import java.util.List;

@lombok.Value
@lombok.Builder
public class SubmitGameCodeResponse {
    private boolean levelCompleted;
    private List<String> enteredCodes;
    private List<String> requiredCodes;
}
