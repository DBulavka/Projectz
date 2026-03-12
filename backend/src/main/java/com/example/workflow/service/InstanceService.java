package com.example.workflow.service;

import com.example.workflow.dto.instance.StartInstanceRequest;
import com.example.workflow.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InstanceService {
    private final RuntimeService runtimeService;
    private final HistoryService historyService;

    public ProcessInstance start(String processId, String versionId, StartInstanceRequest req) {
        Map<String, Object> variables = new HashMap<>();
        if (req.variables() != null) {
            variables.putAll(req.variables());
        }
        if (req.assigneeGroupId() != null) {
            variables.put("assignee", req.assigneeGroupId().toString());
        }
        return runtimeService.startProcessInstanceById(versionId, variables);
    }

    public List<ProcessInstance> list() {
        return runtimeService.createProcessInstanceQuery().orderByStartTime().desc().list();
    }

    public ProcessInstance get(String id) {
        ProcessInstance instance = runtimeService.createProcessInstanceQuery().processInstanceId(id).singleResult();
        if (instance == null) {
            throw new ApiException("Instance not found");
        }
        return instance;
    }

    public ProcessInstance cancel(String id) {
        ProcessInstance instance = get(id);
        runtimeService.deleteProcessInstance(id, "Cancelled by user");
        return instance;
    }

    public List<HistoricActivityInstance> history(String id) {
        return historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(id)
                .orderByHistoricActivityInstanceStartTime().asc()
                .list();
    }
}
