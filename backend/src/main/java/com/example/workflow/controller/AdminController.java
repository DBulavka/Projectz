package com.example.workflow.controller;

import com.example.workflow.repository.ProcessDefinitionMetaRepository;
import com.example.workflow.repository.ProcessInstanceMetaRepository;
import com.example.workflow.service.TaskServiceApp;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final ProcessDefinitionMetaRepository metaRepository;
    private final ProcessInstanceMetaRepository instanceRepository;
    private final TaskServiceApp taskService;

    @GetMapping("/processes") public Object processes() { return metaRepository.findAll(); }
    @GetMapping("/instances") public Object instances() { return instanceRepository.findAll(); }
    @GetMapping("/tasks") public Object tasks() { return taskService.adminTasks(); }
}
