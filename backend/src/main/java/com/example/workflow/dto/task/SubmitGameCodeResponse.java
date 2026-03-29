package com.example.workflow.dto.task;

import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SubmitGameCodeResponse {
    boolean correct;
    boolean levelCompleted;
    List<String> enteredCodes;
}
