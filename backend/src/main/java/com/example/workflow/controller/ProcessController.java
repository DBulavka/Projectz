package com.example.workflow.controller;

import com.example.workflow.dto.audit.AuditLogDto;
import com.example.workflow.dto.process.ProcessMetaDto;
import com.example.workflow.dto.process.ProcessMetaRequest;
import com.example.workflow.dto.process.ProcessVersionDto;
import com.example.workflow.dto.process.ProcessVersionRequest;
import com.example.workflow.mapper.ProcessMapper;
import com.example.workflow.service.ProcessService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/processes")
@RequiredArgsConstructor
public class ProcessController {
    private final ProcessService processService;
    private final ProcessMapper processMapper;

    @GetMapping
    public List<ProcessMetaDto> list() {
        return processMapper.toMetaDtoList(processService.list());
    }

    @PostMapping
    public ProcessMetaDto create(@Valid @RequestBody ProcessMetaRequest req) {
        return processMapper.toDto(processService.create(req));
    }

    @GetMapping("/{id}")
    public ProcessMetaDto get(@PathVariable Long id) {
        return processMapper.toDto(processService.get(id));
    }

    @PutMapping("/{id}")
    public ProcessMetaDto update(@PathVariable Long id, @Valid @RequestBody ProcessMetaRequest req) {
        return processMapper.toDto(processService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        processService.delete(id);
    }

    @GetMapping("/{id}/versions")
    public List<ProcessVersionDto> listVersions(@PathVariable Long id) {
        return processMapper.toVersionDtoList(processService.listVersions(id));
    }

    @PostMapping("/{id}/versions")
    public ProcessVersionDto createVersion(@PathVariable Long id, @Valid @RequestBody ProcessVersionRequest req) {
        return processMapper.toDto(processService.createVersion(id, req));
    }

    @GetMapping("/{id}/versions/{versionId}")
    public ProcessVersionDto getVersion(@PathVariable Long id, @PathVariable Long versionId) {
        return processMapper.toDto(processService.getVersion(id, versionId));
    }

    @PutMapping("/{id}/versions/{versionId}/bpmn")
    public ProcessVersionDto updateVersion(@PathVariable Long id, @PathVariable Long versionId, @Valid @RequestBody ProcessVersionRequest req) {
        return processMapper.toDto(processService.updateVersionBpmn(id, versionId, req));
    }

    @PostMapping("/{id}/versions/{versionId}/publish")
    public ProcessVersionDto publish(@PathVariable Long id, @PathVariable Long versionId) {
        return processMapper.toDto(processService.publish(id, versionId));
    }

    @GetMapping("/{id}/audit")
    public List<AuditLogDto> audit(@PathVariable Long id) {
        return processMapper.toAuditDtoList(processService.processAudit(id));
    }
}
