package com.example.workflow.controller;

import com.example.workflow.dto.task.CompleteTaskRequest;
import com.example.workflow.service.TaskServiceApp;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskServiceApp taskService;

    @GetMapping("/my") public Object my() { return taskService.myTasks(); }
    @GetMapping("/{taskId}") public Object get(@PathVariable String taskId) { return taskService.getTask(taskId); }
    @PostMapping("/{taskId}/complete") public void complete(@PathVariable String taskId, @RequestBody(required = false) CompleteTaskRequest req) {
        taskService.complete(taskId, req == null ? new CompleteTaskRequest(java.util.Map.of()) : req);
    }
}
