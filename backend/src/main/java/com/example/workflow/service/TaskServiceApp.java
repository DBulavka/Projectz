package com.example.workflow.service;

import com.example.workflow.dto.task.CompleteTaskRequest;
import com.example.workflow.entity.GroupType;
import com.example.workflow.entity.ProcessInstanceMeta;
import com.example.workflow.entity.UserGroup;
import com.example.workflow.entity.UserGroupMembership;
import com.example.workflow.exception.ApiException;
import com.example.workflow.repository.GroupTypeRepository;
import com.example.workflow.repository.ProcessInstanceMetaRepository;
import com.example.workflow.repository.UserGroupMembershipRepository;
import com.example.workflow.repository.UserGroupRepository;
import com.example.workflow.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.HistoryService;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    private final ProcessInstanceMetaRepository instanceRepository;
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
            for (Task task : taskService.createTaskQuery().taskAssignee(groupId.toString()).list()) {
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
