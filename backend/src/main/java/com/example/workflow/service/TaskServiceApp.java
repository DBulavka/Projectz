package com.example.workflow.service;

import com.example.workflow.dto.task.CompleteTaskRequest;
import com.example.workflow.dto.task.SubmitGameCodeResponse;
import com.example.workflow.entity.GameLevelCode;
import com.example.workflow.entity.GroupType;
import com.example.workflow.entity.ProcessDefinitionMeta;
import com.example.workflow.entity.ProcessInstanceMeta;
import com.example.workflow.entity.UserGroup;
import com.example.workflow.entity.UserGroupMembership;
import com.example.workflow.exception.ApiException;
import com.example.workflow.repository.GameLevelCodeRepository;
import com.example.workflow.repository.GroupTypeRepository;
import com.example.workflow.repository.ProcessDefinitionMetaRepository;
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
    private final ProcessInstanceMetaRepository instanceRepository;
    private final SecurityUtils securityUtils;
    private final UserGroupMembershipRepository membershipRepository;
    private final UserGroupRepository userGroupRepository;
    private final GroupTypeRepository groupTypeRepository;
    private final ProcessDefinitionMetaRepository processDefinitionMetaRepository;
    private final GameLevelCodeRepository gameLevelCodeRepository;

    private static final String GAME_CATEGORY = "game";

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
        ProcessDefinitionMeta processMeta = resolveProcessMeta(task);

        if (!GAME_CATEGORY.equalsIgnoreCase(processMeta.getCategory())) {
            taskService.complete(task.getId(), variablesOrEmpty(req));
            return;
        }

        SubmitGameCodeResponse response = submitGameCodeInternal(task, processMeta, req.code(), variablesOrEmpty(req));
        if (!response.levelCompleted()) {
            throw new ApiException("Not all level codes entered yet");
        }
    }

    public SubmitGameCodeResponse submitGameCode(String taskId, String code) {
        Task task = getTask(taskId);
        ProcessDefinitionMeta processMeta = resolveProcessMeta(task);
        return submitGameCodeInternal(task, processMeta, code, Map.of());
    }


    private ProcessDefinitionMeta resolveProcessMeta(Task task) {
        ProcessInstanceMeta instanceMeta = instanceRepository.findByFlowableProcessInstanceId(task.getProcessInstanceId())
                .orElseThrow(() -> new ApiException("Task instance mapping not found"));
        return processDefinitionMetaRepository.findById(instanceMeta.getProcessDefinitionMetaId())
                .orElseThrow(() -> new ApiException("Process not found"));
    }

    private SubmitGameCodeResponse submitGameCodeInternal(Task task,
                                                          ProcessDefinitionMeta processMeta,
                                                          String inputCode,
                                                          Map<String, Object> completionVariables) {
        if (!GAME_CATEGORY.equalsIgnoreCase(processMeta.getCategory())) {
            throw new ApiException("Codes are supported only for game processes");
        }

        String levelKey = task.getTaskDefinitionKey();
        if (levelKey == null || levelKey.isBlank()) {
            throw new ApiException("Game level key not found");
        }

        List<GameLevelCode> levelCodes = gameLevelCodeRepository.findByProcessDefinitionMetaIdAndLevelKeyOrderByCreatedAtAsc(processMeta.getId(), levelKey);
        if (levelCodes.isEmpty()) {
            throw new ApiException("No codes configured for this level");
        }

        if (inputCode == null || inputCode.isBlank()) {
            throw new ApiException("Code is required for game level completion");
        }

        Set<String> requiredCodes = levelCodes.stream()
                .map(GameLevelCode::getValue)
                .collect(Collectors.toSet());

        if (!requiredCodes.contains(inputCode)) {
            throw new ApiException("Invalid level code");
        }

        String enteredCodesVariable = "gameLevelEnteredCodes_" + levelKey;
        Set<String> enteredCodes = readEnteredCodes(task.getId(), enteredCodesVariable);
        enteredCodes.add(inputCode);
        taskService.setVariable(task.getId(), enteredCodesVariable, new ArrayList<>(enteredCodes));

        boolean completed = enteredCodes.containsAll(requiredCodes);
        if (completed) {
            taskService.complete(task.getId(), completionVariables);
        }

        return new SubmitGameCodeResponse(
                completed,
                enteredCodes.stream().sorted().toList(),
                requiredCodes.stream().sorted().toList()
        );
    }

    private void checkTaskAccess(Task task) {
        ProcessInstanceMeta meta = instanceRepository.findByFlowableProcessInstanceId(task.getProcessInstanceId())
                .orElseThrow(() -> new ApiException("Task instance mapping not found"));
        if (!securityUtils.isAdmin() && !meta.getOwnerId().equals(securityUtils.currentUserId())) throw new ApiException("Forbidden");
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
