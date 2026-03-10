package com.example.workflow.controller;

import com.example.workflow.dto.instance.ProcessInstanceDto;
import com.example.workflow.dto.process.ProcessMetaDto;
import com.example.workflow.dto.task.HistoricTaskDto;
import com.example.workflow.mapper.InstanceMapper;
import com.example.workflow.mapper.ProcessMapper;
import com.example.workflow.mapper.TaskMapper;
import com.example.workflow.repository.ProcessDefinitionMetaRepository;
import com.example.workflow.repository.ProcessInstanceMetaRepository;
import com.example.workflow.service.TaskServiceApp;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final ProcessDefinitionMetaRepository metaRepository;
    private final ProcessInstanceMetaRepository instanceRepository;
    private final TaskServiceApp taskService;
    private final ProcessMapper processMapper;
    private final InstanceMapper instanceMapper;
    private final TaskMapper taskMapper;

    @GetMapping("/processes")
    public List<ProcessMetaDto> processes() {
        return processMapper.toMetaDtoList(metaRepository.findAll());
    }

    @GetMapping("/instances")
    public List<ProcessInstanceDto> instances() {
        return instanceMapper.toDtoList(instanceRepository.findAll());
    }

    @GetMapping("/tasks")
    public List<HistoricTaskDto> tasks() {
        return taskMapper.toHistoricTaskDtoList(taskService.adminTasks());
    }
}
