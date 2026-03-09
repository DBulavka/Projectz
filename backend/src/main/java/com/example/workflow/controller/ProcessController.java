package com.example.workflow.controller;

import com.example.workflow.dto.process.ProcessMetaRequest;
import com.example.workflow.dto.process.ProcessVersionRequest;
import com.example.workflow.service.ProcessService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/processes")
@RequiredArgsConstructor
public class ProcessController {
    private final ProcessService processService;

    @GetMapping public Object list() { return processService.list(); }
    @PostMapping public Object create(@Valid @RequestBody ProcessMetaRequest req) { return processService.create(req); }
    @GetMapping("/{id}") public Object get(@PathVariable Long id) { return processService.get(id); }
    @PutMapping("/{id}") public Object update(@PathVariable Long id, @Valid @RequestBody ProcessMetaRequest req) { return processService.update(id, req); }
    @DeleteMapping("/{id}") public void delete(@PathVariable Long id) { processService.delete(id); }

    @GetMapping("/{id}/versions") public Object listVersions(@PathVariable Long id) { return processService.listVersions(id); }
    @PostMapping("/{id}/versions") public Object createVersion(@PathVariable Long id, @Valid @RequestBody ProcessVersionRequest req) { return processService.createVersion(id, req); }
    @GetMapping("/{id}/versions/{versionId}") public Object getVersion(@PathVariable Long id, @PathVariable Long versionId) { return processService.getVersion(id, versionId); }
    @PutMapping("/{id}/versions/{versionId}/bpmn") public Object updateVersion(@PathVariable Long id, @PathVariable Long versionId, @Valid @RequestBody ProcessVersionRequest req) { return processService.updateVersionBpmn(id, versionId, req); }
    @PostMapping("/{id}/versions/{versionId}/publish") public Object publish(@PathVariable Long id, @PathVariable Long versionId) { return processService.publish(id, versionId); }

    @GetMapping("/{id}/audit") public Object audit(@PathVariable Long id) { return processService.processAudit(id); }
}
