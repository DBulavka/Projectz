package com.example.workflow.controller;

import com.example.workflow.dto.instance.HistoricActivityDto;
import com.example.workflow.dto.instance.ProcessInstanceDto;
import com.example.workflow.dto.instance.StartInstanceRequest;
import com.example.workflow.mapper.InstanceMapper;
import com.example.workflow.service.InstanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class InstanceController {
    private final InstanceService instanceService;
    private final InstanceMapper instanceMapper;

    @PostMapping("/api/processes/{processId}/start")
    public ProcessInstanceDto start(@PathVariable String processId, @RequestBody(required = false) StartInstanceRequest req) {
        return instanceMapper.toDto(instanceService.start(processId, req == null
                ? StartInstanceRequest.builder().businessKey(null).variables(java.util.Map.of()).build()
                : req));
    }

    @GetMapping("/api/instances")
    public List<ProcessInstanceDto> list() {
        return instanceMapper.toDtoList(instanceService.list());
    }

    @GetMapping("/api/instances/{instanceId}")
    public ProcessInstanceDto get(@PathVariable String instanceId) {
        return instanceMapper.toDto(instanceService.get(instanceId));
    }

    @PostMapping("/api/instances/{instanceId}/cancel")
    public ProcessInstanceDto cancel(@PathVariable String instanceId) {
        return instanceMapper.toDto(instanceService.cancel(instanceId));
    }

    @GetMapping("/api/instances/{instanceId}/history")
    public List<HistoricActivityDto> history(@PathVariable String instanceId) {
        return instanceMapper.toHistoryDtoList(instanceService.history(instanceId));
    }
}
