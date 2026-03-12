package com.example.workflow.controller;

import com.example.workflow.dto.audit.AuditLogDto;
import com.example.workflow.dto.process.GameLevelCodesDto;
import com.example.workflow.dto.process.GameLevelCodesRequest;
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
import java.util.UUID;

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
    public ProcessMetaDto get(@PathVariable UUID id) {
        return processMapper.toDto(processService.get(id));
    }

    @PutMapping("/{id}")
    public ProcessMetaDto update(@PathVariable UUID id, @Valid @RequestBody ProcessMetaRequest req) {
        return processMapper.toDto(processService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        processService.delete(id);
    }

    @GetMapping("/{id}/versions")
    public List<ProcessVersionDto> listVersions(@PathVariable UUID id) {
        return processMapper.toVersionDtoList(processService.listVersions(id));
    }

    @PostMapping("/{id}/versions")
    public ProcessVersionDto createVersion(@PathVariable UUID id, @Valid @RequestBody ProcessVersionRequest req) {
        return processMapper.toDto(processService.createVersion(id, req));
    }

    @GetMapping("/{id}/versions/{versionId}")
    public ProcessVersionDto getVersion(@PathVariable UUID id, @PathVariable UUID versionId) {
        return processMapper.toDto(processService.getVersion(id, versionId));
    }

    @PutMapping("/{id}/versions/{versionId}/bpmn")
    public ProcessVersionDto updateVersion(@PathVariable UUID id, @PathVariable UUID versionId, @Valid @RequestBody ProcessVersionRequest req) {
        return processMapper.toDto(processService.updateVersionBpmn(id, versionId, req));
    }

    @PostMapping("/{id}/versions/{versionId}/publish")
    public ProcessVersionDto publish(@PathVariable UUID id, @PathVariable UUID versionId) {
        return processMapper.toDto(processService.publish(id, versionId));
    }

    @GetMapping("/{id}/audit")
    public List<AuditLogDto> audit(@PathVariable UUID id) {
        return processMapper.toAuditDtoList(processService.processAudit(id));
    }

    @GetMapping("/{id}/levels/{levelKey}/codes")
    public GameLevelCodesDto getLevelCodes(@PathVariable UUID id, @PathVariable String levelKey) {
        return new GameLevelCodesDto(levelKey, processService.getLevelCodes(id, levelKey));
    }

    @PutMapping("/{id}/levels/{levelKey}/codes")
    public GameLevelCodesDto replaceLevelCodes(@PathVariable UUID id,
                                               @PathVariable String levelKey,
                                               @Valid @RequestBody GameLevelCodesRequest req) {
        return new GameLevelCodesDto(levelKey, processService.replaceLevelCodes(id, levelKey, req));
    }
}
