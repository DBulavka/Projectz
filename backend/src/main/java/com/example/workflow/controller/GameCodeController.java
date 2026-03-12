package com.example.workflow.controller;

import com.example.workflow.dto.task.SubmitGameCodeRequest;
import com.example.workflow.dto.task.SubmitGameCodeResponse;
import com.example.workflow.service.TaskServiceApp;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
public class GameCodeController {
    private final TaskServiceApp taskService;

    @PostMapping("/tasks/{taskId}/codes")
    public SubmitGameCodeResponse submitCode(@PathVariable String taskId,
                                             @Valid @RequestBody SubmitGameCodeRequest req) {
        return taskService.submitGameCode(taskId, req.code());
    }
}
