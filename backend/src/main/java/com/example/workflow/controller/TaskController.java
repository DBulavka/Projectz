package com.example.workflow.controller;

import com.example.workflow.dto.task.CompleteTaskRequest;
import com.example.workflow.dto.task.TaskDto;
import com.example.workflow.mapper.TaskMapper;
import com.example.workflow.service.TaskServiceApp;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskServiceApp taskService;
    private final TaskMapper taskMapper;

    @GetMapping("/my")
    public List<TaskDto> my(@RequestParam(required = false) String groupType) {
        return taskMapper.toTaskDtoList(taskService.myTasks(groupType));
    }

    @GetMapping("/{taskId}")
    public TaskDto get(@PathVariable String taskId) {
        return taskMapper.toDto(taskService.getTask(taskId));
    }

    @PostMapping("/{taskId}/complete")
    public void complete(@PathVariable String taskId, @RequestBody(required = false) CompleteTaskRequest req) {
        taskService.complete(taskId, req == null ? new CompleteTaskRequest(java.util.Map.of()) : req);
    }
}
