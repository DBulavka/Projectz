package com.example.workflow.service;

import com.example.workflow.dto.task.CompleteTaskRequest;
import com.example.workflow.entity.ProcessInstanceMeta;
import com.example.workflow.exception.ApiException;
import com.example.workflow.repository.ProcessInstanceMetaRepository;
import com.example.workflow.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.HistoryService;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceApp {
    private final TaskService taskService;
    private final HistoryService historyService;
    private final ProcessInstanceMetaRepository instanceRepository;
    private final SecurityUtils securityUtils;

    public List<Task> myTasks() {
        return taskService.createTaskQuery().taskAssignee(securityUtils.currentEmail()).list();
    }

    public Task getTask(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) throw new ApiException("Task not found");
        checkTaskAccess(task);
        return task;
    }

    public void complete(String taskId, CompleteTaskRequest req) {
        Task task = getTask(taskId);
        taskService.complete(task.getId(), req.variables());
    }

    private void checkTaskAccess(Task task) {
        ProcessInstanceMeta meta = instanceRepository.findByFlowableProcessInstanceId(task.getProcessInstanceId())
                .orElseThrow(() -> new ApiException("Task instance mapping not found"));
        if (!securityUtils.isAdmin() && !meta.getOwnerId().equals(securityUtils.currentUserId())) throw new ApiException("Forbidden");
    }

    public List<HistoricTaskInstance> adminTasks() {
        return historyService.createHistoricTaskInstanceQuery().orderByTaskCreateTime().desc().list();
    }
}
