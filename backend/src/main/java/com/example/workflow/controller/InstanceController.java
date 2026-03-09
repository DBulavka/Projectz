package com.example.workflow.controller;

import com.example.workflow.dto.instance.StartInstanceRequest;
import com.example.workflow.service.InstanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class InstanceController {
    private final InstanceService instanceService;

    @PostMapping("/api/processes/{id}/versions/{versionId}/start")
    public Object start(@PathVariable Long id, @PathVariable Long versionId, @RequestBody(required = false) StartInstanceRequest req) {
        return instanceService.start(id, versionId, req == null ? new StartInstanceRequest(java.util.Map.of()) : req);
    }

    @GetMapping("/api/instances") public Object list() { return instanceService.list(); }
    @GetMapping("/api/instances/{instanceId}") public Object get(@PathVariable Long instanceId) { return instanceService.get(instanceId); }
    @PostMapping("/api/instances/{instanceId}/cancel") public Object cancel(@PathVariable Long instanceId) { return instanceService.cancel(instanceId); }
    @GetMapping("/api/instances/{instanceId}/history") public Object history(@PathVariable Long instanceId) { return instanceService.history(instanceId); }
}
