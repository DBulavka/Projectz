package com.example.workflow.service;

import com.example.workflow.dto.process.ProcessMetaRequest;
import com.example.workflow.dto.process.ProcessVersionRequest;
import com.example.workflow.entity.AuditLog;
import com.example.workflow.entity.ProcessDefinitionMeta;
import com.example.workflow.entity.ProcessDefinitionVersion;
import com.example.workflow.enums.VersionStatus;
import com.example.workflow.exception.ApiException;
import com.example.workflow.repository.AuditLogRepository;
import com.example.workflow.repository.ProcessDefinitionMetaRepository;
import com.example.workflow.repository.ProcessDefinitionVersionRepository;
import com.example.workflow.repository.UserGroupRepository;
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
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProcessService {
    private final ProcessDefinitionMetaRepository metaRepository;
    private final ProcessDefinitionVersionRepository versionRepository;
    private final AuditLogRepository auditLogRepository;
    private final RepositoryService repositoryService;
    private final SecurityUtils securityUtils;
    private final AuditService auditService;
    private final UserGroupRepository userGroupRepository;

    public List<ProcessDefinitionMeta> list() {
        if (securityUtils.isAdmin()) return metaRepository.findAll();
        Set<UUID> groupIds = securityUtils.currentUserGroupIds();
        if (groupIds.isEmpty()) return List.of();
        return metaRepository.findByOwnerGroupIdIn(groupIds);
    }

    public ProcessDefinitionMeta create(ProcessMetaRequest req) {
        if (req.ownerGroupId() == null) throw new ApiException("ownerGroupId is required");
        if (!userGroupRepository.existsById(req.ownerGroupId())) throw new ApiException("Group not found");
        if (!securityUtils.canManageGroup(req.ownerGroupId())) throw new ApiException("Forbidden");

        Instant now = Instant.now();
        ProcessDefinitionMeta meta = metaRepository.save(ProcessDefinitionMeta.builder()
                .ownerGroupId(req.ownerGroupId())
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

    public ProcessDefinitionMeta get(UUID id) { return accessibleMeta(id); }

    public ProcessDefinitionMeta update(UUID id, ProcessMetaRequest req) {
        ProcessDefinitionMeta meta = manageableMeta(id);
        meta.setName(req.name());
        meta.setDescription(req.description());
        meta.setCategory(req.category());
        meta.setUpdatedAt(Instant.now());
        auditService.log(securityUtils.currentUserId(), "PROCESS", meta.getId().toString(), "UPDATE", req);
        return metaRepository.save(meta);
    }

    public void delete(UUID id) {
        manageableMeta(id);
        metaRepository.deleteById(id);
        auditService.log(securityUtils.currentUserId(), "PROCESS", id.toString(), "DELETE", null);
    }

    public List<ProcessDefinitionVersion> listVersions(UUID processId) {
        accessibleMeta(processId);
        return versionRepository.findByProcessDefinitionMetaIdOrderByVersionNumberDesc(processId);
    }

    public ProcessDefinitionVersion createVersion(UUID processId, ProcessVersionRequest req) {
        manageableMeta(processId);
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

    public ProcessDefinitionVersion getVersion(UUID processId, UUID versionId) {
        accessibleMeta(processId);
        var v = versionRepository.findById(versionId).orElseThrow(() -> new ApiException("Version not found"));
        if (!v.getProcessDefinitionMetaId().equals(processId)) throw new ApiException("Version mismatch");
        return v;
    }

    public ProcessDefinitionVersion updateVersionBpmn(UUID processId, UUID versionId, ProcessVersionRequest req) {
        manageableMeta(processId);
        ProcessDefinitionVersion v = getVersion(processId, versionId);
        if (v.getStatus() == VersionStatus.PUBLISHED) throw new ApiException("Published version cannot be edited");
        v.setBpmnXml(req.bpmnXml());
        return versionRepository.save(v);
    }

    public ProcessDefinitionVersion publish(UUID processId, UUID versionId) {
        manageableMeta(processId);
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

    public List<AuditLog> processAudit(UUID processId) {
        accessibleMeta(processId);
        return auditLogRepository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc("PROCESS", processId.toString());
    }

    private ProcessDefinitionMeta accessibleMeta(UUID id) {
        ProcessDefinitionMeta meta = metaRepository.findById(id).orElseThrow(() -> new ApiException("Process not found"));
        if (!securityUtils.hasGroupAccess(meta.getOwnerGroupId())) throw new ApiException("Forbidden");
        return meta;
    }

    private ProcessDefinitionMeta manageableMeta(UUID id) {
        ProcessDefinitionMeta meta = metaRepository.findById(id).orElseThrow(() -> new ApiException("Process not found"));
        if (!securityUtils.canManageGroup(meta.getOwnerGroupId())) throw new ApiException("Forbidden");
        return meta;
    }
}
