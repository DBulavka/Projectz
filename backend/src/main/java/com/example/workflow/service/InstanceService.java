package com.example.workflow.service;

import com.example.workflow.dto.instance.StartInstanceRequest;
import com.example.workflow.entity.ProcessDefinitionMeta;
import com.example.workflow.entity.ProcessDefinitionVersion;
import com.example.workflow.entity.ProcessInstanceMeta;
import com.example.workflow.enums.InstanceStatus;
import com.example.workflow.enums.VersionStatus;
import com.example.workflow.exception.ApiException;
import com.example.workflow.repository.ProcessDefinitionMetaRepository;
import com.example.workflow.repository.ProcessDefinitionVersionRepository;
import com.example.workflow.repository.ProcessInstanceMetaRepository;
import com.example.workflow.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InstanceService {
    private final ProcessDefinitionMetaRepository metaRepository;
    private final ProcessDefinitionVersionRepository versionRepository;
    private final ProcessInstanceMetaRepository instanceRepository;
    private final RuntimeService runtimeService;
    private final HistoryService historyService;
    private final SecurityUtils securityUtils;

    public ProcessInstanceMeta start(UUID processId, UUID versionId, StartInstanceRequest req) {
        ProcessDefinitionMeta meta = metaRepository.findById(processId).orElseThrow(() -> new ApiException("Process not found"));
        if (!securityUtils.isAdmin() && !securityUtils.hasGroupAccess(meta.getOwnerGroupId())) throw new ApiException("Forbidden");
        ProcessDefinitionVersion version = versionRepository.findById(versionId).orElseThrow(() -> new ApiException("Version not found"));
        if (version.getStatus() != VersionStatus.PUBLISHED) throw new ApiException("Only published version can be started");

        ProcessInstance instance = runtimeService.startProcessInstanceById(version.getFlowableProcessDefinitionId(), req.variables());
        return instanceRepository.save(ProcessInstanceMeta.builder()
                .processDefinitionMetaId(processId)
                .processDefinitionVersionId(versionId)
                .ownerId(securityUtils.currentUserId())
                .flowableProcessInstanceId(instance.getId())
                .status(InstanceStatus.RUNNING)
                .startedAt(Instant.now())
                .build());
    }

    public List<ProcessInstanceMeta> list() {
        if (securityUtils.isAdmin()) return instanceRepository.findAll();
        return instanceRepository.findByOwnerIdOrderByStartedAtDesc(securityUtils.currentUserId());
    }

    public ProcessInstanceMeta get(UUID id) {
        ProcessInstanceMeta instance = instanceRepository.findById(id).orElseThrow(() -> new ApiException("Instance not found"));
        if (!securityUtils.isAdmin() && !instance.getOwnerId().equals(securityUtils.currentUserId())) throw new ApiException("Forbidden");

        if (runtimeService.createProcessInstanceQuery().processInstanceId(instance.getFlowableProcessInstanceId()).singleResult() == null
                && instance.getStatus() == InstanceStatus.RUNNING) {
            instance.setStatus(InstanceStatus.COMPLETED);
            instance.setEndedAt(Instant.now());
            instanceRepository.save(instance);
        }
        return instance;
    }

    public ProcessInstanceMeta cancel(UUID id) {
        ProcessInstanceMeta instance = get(id);
        runtimeService.deleteProcessInstance(instance.getFlowableProcessInstanceId(), "Cancelled by user");
        instance.setStatus(InstanceStatus.CANCELLED);
        instance.setEndedAt(Instant.now());
        return instanceRepository.save(instance);
    }

    public List<HistoricActivityInstance> history(UUID id) {
        ProcessInstanceMeta instance = get(id);
        return historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(instance.getFlowableProcessInstanceId())
                .orderByHistoricActivityInstanceStartTime().asc()
                .list();
    }
}
