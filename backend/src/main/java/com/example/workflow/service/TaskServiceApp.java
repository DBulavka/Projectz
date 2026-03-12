package com.example.workflow.service;

import com.example.workflow.dto.task.CompleteTaskRequest;
import com.example.workflow.dto.task.SubmitGameCodeResponse;
import com.example.workflow.entity.GroupType;
import com.example.workflow.entity.UserGroup;
import com.example.workflow.entity.UserGroupMembership;
import com.example.workflow.exception.ApiException;
import com.example.workflow.repository.GroupTypeRepository;
import com.example.workflow.repository.UserGroupMembershipRepository;
import com.example.workflow.repository.UserGroupRepository;
import com.example.workflow.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceApp {
    private final TaskService taskService;
    private final HistoryService historyService;
    private final RepositoryService repositoryService;
    private final SecurityUtils securityUtils;
    private final UserGroupMembershipRepository membershipRepository;
    private final UserGroupRepository userGroupRepository;
    private final GroupTypeRepository groupTypeRepository;

    public List<Task> myTasks(String groupTypeCode) {
        Map<String, Task> tasksById = new LinkedHashMap<>();
        for (Task task : taskService.createTaskQuery().taskAssignee(securityUtils.currentEmail()).list()) {
            tasksById.put(task.getId(), task);
        }

        for (UUID groupId : resolveUserGroupIds(groupTypeCode)) {
            for (Task task : taskService.createTaskQuery().processInstanceBusinessKeyLikeIgnoreCase(groupId.toString()).list()) {
                tasksById.put(task.getId(), task);
            }
        }

        List<Task> result = new ArrayList<>(tasksById.values());
        result.sort(Comparator.comparing(Task::getCreateTime, Comparator.nullsLast(Comparator.naturalOrder())).reversed());
        return result;
    }

    public Task getTask(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) throw new ApiException("Task not found");
        return task;
    }

    public void complete(String taskId, CompleteTaskRequest req) {
        Task task = getTask(taskId);
        taskService.complete(task.getId(), variablesOrEmpty(req));
    }

    public SubmitGameCodeResponse submitGameCode(String taskId, String code) {
        Task task = getTask(taskId);
        var processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(task.getProcessDefinitionId())
                .singleResult();
        if (processDefinition == null || !"game".equalsIgnoreCase(processDefinition.getCategory())) {
            throw new ApiException("Codes are supported only for game processes");
        }
        throw new ApiException("Game code validation requires process metadata and is temporarily unavailable");
    }

    public List<HistoricTaskInstance> adminTasks() {
        return historyService.createHistoricTaskInstanceQuery().orderByTaskCreateTime().desc().list();
    }

    private Map<String, Object> variablesOrEmpty(CompleteTaskRequest req) {
        return req.variables() == null ? Map.of() : req.variables();
    }

    @SuppressWarnings("unchecked")
    private Set<String> readEnteredCodes(String taskId, String variableName) {
        Object raw = taskService.getVariable(taskId, variableName);
        if (raw == null) {
            return new java.util.HashSet<>();
        }
        if (raw instanceof Collection<?> collection) {
            return collection.stream().map(String::valueOf).collect(Collectors.toSet());
        }
        return new java.util.HashSet<>();
    }

    private Set<UUID> resolveUserGroupIds(String groupTypeCode) {
        UUID userId = securityUtils.currentUserId();
        List<UserGroupMembership> memberships = membershipRepository.findByUserId(userId);
        if (memberships.isEmpty()) {
            return Set.of();
        }

        Set<UUID> membershipGroupIds = memberships.stream()
                .map(UserGroupMembership::getGroupId)
                .collect(Collectors.toSet());

        if (groupTypeCode == null || groupTypeCode.isBlank()) {
            return membershipGroupIds;
        }

        GroupType groupType = groupTypeRepository.findByCode(groupTypeCode)
                .orElseThrow(() -> new ApiException("Group type not found"));

        return userGroupRepository.findByIdInAndGroupTypeId(membershipGroupIds, groupType.getId()).stream()
                .map(UserGroup::getId)
                .collect(Collectors.toSet());
    }
}
