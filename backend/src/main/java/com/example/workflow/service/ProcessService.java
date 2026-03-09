package com.example.workflow.service;

import com.example.workflow.dto.process.ProcessMetaRequest;
import com.example.workflow.dto.process.ProcessVersionRequest;
import com.example.workflow.entity.ProcessDefinitionMeta;
import com.example.workflow.entity.ProcessDefinitionVersion;
import com.example.workflow.enums.VersionStatus;
import com.example.workflow.exception.ApiException;
import com.example.workflow.repository.AuditLogRepository;
import com.example.workflow.repository.ProcessDefinitionMetaRepository;
import com.example.workflow.repository.ProcessDefinitionVersionRepository;
import com.example.workflow.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProcessService {
    private final ProcessDefinitionMetaRepository metaRepository;
    private final ProcessDefinitionVersionRepository versionRepository;
    private final AuditLogRepository auditLogRepository;
    private final RepositoryService repositoryService;
    private final SecurityUtils securityUtils;
    private final AuditService auditService;

    public List<ProcessDefinitionMeta> list() {
        if (securityUtils.isAdmin()) return metaRepository.findAll();
        return metaRepository.findByOwnerId(securityUtils.currentUserId());
    }

    public ProcessDefinitionMeta create(ProcessMetaRequest req) {
        Instant now = Instant.now();
        ProcessDefinitionMeta meta = metaRepository.save(ProcessDefinitionMeta.builder()
                .ownerId(securityUtils.currentUserId())
                .key(req.key())
                .name(req.name())
                .description(req.description())
                .category(req.category())
                .createdAt(now)
                .updatedAt(now)
                .build());
        auditService.log(securityUtils.currentUserId(), "PROCESS", meta.getId().toString(), "CREATE", req);
        return meta;
    }

    public ProcessDefinitionMeta get(Long id) { return ownedMeta(id); }

    public ProcessDefinitionMeta update(Long id, ProcessMetaRequest req) {
        ProcessDefinitionMeta meta = ownedMeta(id);
        meta.setName(req.name());
        meta.setDescription(req.description());
        meta.setCategory(req.category());
        meta.setUpdatedAt(Instant.now());
        auditService.log(securityUtils.currentUserId(), "PROCESS", meta.getId().toString(), "UPDATE", req);
        return metaRepository.save(meta);
    }

    public void delete(Long id) {
        ProcessDefinitionMeta meta = ownedMeta(id);
        metaRepository.delete(meta);
        auditService.log(securityUtils.currentUserId(), "PROCESS", id.toString(), "DELETE", null);
    }

    public List<ProcessDefinitionVersion> listVersions(Long processId) {
        ownedMeta(processId);
        return versionRepository.findByProcessDefinitionMetaIdOrderByVersionNumberDesc(processId);
    }

    public ProcessDefinitionVersion createVersion(Long processId, ProcessVersionRequest req) {
        ownedMeta(processId);
        int count = versionRepository.countByProcessDefinitionMetaId(processId);
        ProcessDefinitionVersion v = versionRepository.save(ProcessDefinitionVersion.builder()
                .processDefinitionMetaId(processId)
                .versionNumber(count + 1)
                .bpmnXml(req.bpmnXml())
                .status(VersionStatus.DRAFT)
                .createdAt(Instant.now())
                .build());
        auditService.log(securityUtils.currentUserId(), "VERSION", v.getId().toString(), "CREATE", null);
        return v;
    }

    public ProcessDefinitionVersion getVersion(Long processId, Long versionId) {
        ownedMeta(processId);
        var v = versionRepository.findById(versionId).orElseThrow(() -> new ApiException("Version not found"));
        if (!v.getProcessDefinitionMetaId().equals(processId)) throw new ApiException("Version mismatch");
        return v;
    }

    public ProcessDefinitionVersion updateVersionBpmn(Long processId, Long versionId, ProcessVersionRequest req) {
        ProcessDefinitionVersion v = getVersion(processId, versionId);
        if (v.getStatus() == VersionStatus.PUBLISHED) throw new ApiException("Published version cannot be edited");
        v.setBpmnXml(req.bpmnXml());
        return versionRepository.save(v);
    }

    public ProcessDefinitionVersion publish(Long processId, Long versionId) {
        ProcessDefinitionVersion v = getVersion(processId, versionId);
        if (v.getStatus() != VersionStatus.DRAFT) throw new ApiException("Only draft can be published");

        try {
            BpmnXMLConverter converter = new BpmnXMLConverter();
            BpmnModel model = converter.convertToBpmnModel(() -> new ByteArrayInputStream(v.getBpmnXml().getBytes(StandardCharsets.UTF_8)), false, false);
            if (model.getMainProcess() == null) throw new ApiException("Invalid BPMN: no process");
        } catch (Exception e) {
            throw new ApiException("Invalid BPMN XML: " + e.getMessage());
        }

        Deployment deployment = repositoryService.createDeployment()
                .name("proc-" + processId + "-v" + versionId)
                .addString("process.bpmn20.xml", v.getBpmnXml())
                .deploy();

        var def = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();
        v.setFlowableDeploymentId(deployment.getId());
        v.setFlowableProcessDefinitionId(def.getId());
        v.setFlowableProcessDefinitionKey(def.getKey());
        v.setStatus(VersionStatus.PUBLISHED);
        v.setPublishedAt(Instant.now());
        auditService.log(securityUtils.currentUserId(), "VERSION", v.getId().toString(), "PUBLISH", null);
        return versionRepository.save(v);
    }

    public List<?> processAudit(Long processId) {
        ownedMeta(processId);
        return auditLogRepository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc("PROCESS", processId.toString());
    }

    private ProcessDefinitionMeta ownedMeta(Long id) {
        ProcessDefinitionMeta meta = metaRepository.findById(id).orElseThrow(() -> new ApiException("Process not found"));
        if (!securityUtils.isAdmin() && !meta.getOwnerId().equals(securityUtils.currentUserId())) throw new ApiException("Forbidden");
        return meta;
    }
}
