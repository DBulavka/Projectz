package com.example.workflow.service;

import com.example.workflow.dto.task.CompleteTaskRequest;
import com.example.workflow.dto.task.SubmitGameCodeResponse;
import com.example.workflow.dto.task.TaskDto;
import com.example.workflow.dto.task.TaskGameCodeDto;
import com.example.workflow.dto.task.TaskGameProgressDto;
import com.example.workflow.entity.GameCodeAttempt;
import com.example.workflow.entity.GroupType;
import com.example.workflow.entity.UserGroup;
import com.example.workflow.entity.UserGroupMembership;
import com.example.workflow.exception.ApiException;
import com.example.workflow.repository.GameCodeAttemptRepository;
import com.example.workflow.repository.GameLevelCodeRepository;
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

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
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
    private final GameLevelCodeRepository gameLevelCodeRepository;
    private final GameCodeAttemptRepository gameCodeAttemptRepository;

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


    public List<TaskDto> myTaskDtos(String groupTypeCode) {
        return myTasks(groupTypeCode).stream()
                .map(this::toTaskDto)
                .toList();
    }
    public Task getTask(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) throw new ApiException("Task not found");
        return task;
    }

    public TaskDto getTaskDto(String taskId) {
        return toTaskDto(getTask(taskId));
    }

    public void complete(String taskId, CompleteTaskRequest req) {
        Task task = getTask(taskId);
        taskService.complete(task.getId(), variablesOrEmpty(req));
    }

    public SubmitGameCodeResponse submitGameCode(String taskId, String value) {
        Task task = getTask(taskId);
        var processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(task.getProcessDefinitionId())
                .singleResult();
//        if (processDefinition == null || !"game".equalsIgnoreCase(processDefinition.getCategory())) {
//            throw new ApiException("Codes are supported only for game processes");
//        }

        List<String> requiredCodes = gameLevelCodeRepository
                .findByProcessIdAndLevelKeyOrderByCreatedAtAsc(processDefinition.getId(), task.getTaskDefinitionKey())
                .stream()
                .map(c -> c.getValue().trim())
                .filter(code -> !code.isEmpty())
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(
                                code -> code.toLowerCase(),
                                code -> code,
                                (left, right) -> left,
                                LinkedHashMap::new
                        ),
                        map -> new ArrayList<>(map.values())
                ));

        if (requiredCodes.isEmpty()) {
            throw new ApiException("No codes configured for this level");
        }

        String normalizedValue = value.trim();
        boolean isCorrect = requiredCodes.stream().anyMatch(required -> required.equalsIgnoreCase(normalizedValue));

        gameCodeAttemptRepository.save(GameCodeAttempt.builder()
                .taskId(taskId)
                .processId(processDefinition.getKey())
                .levelKey(task.getTaskDefinitionKey())
                .value(value)
                .correct(isCorrect)
                .createdAt(Instant.now())
                .build());

        List<GameCodeAttempt> attempts = gameCodeAttemptRepository.findByTaskIdOrderByCreatedAtAsc(taskId);
        Set<String> enteredCorrectCodes = attempts.stream()
                .filter(GameCodeAttempt::isCorrect)
                .map(a -> a.getValue().trim().toLowerCase())
                .collect(Collectors.toCollection(HashSet::new));

        boolean levelCompleted = requiredCodes.stream()
                .map(c -> c.toLowerCase())
                .allMatch(enteredCorrectCodes::contains);

        if (levelCompleted) {
            taskService.complete(taskId);
        }

        List<String> enteredCodes = attempts.stream().map(GameCodeAttempt::getValue).toList();
        return new SubmitGameCodeResponse(levelCompleted, enteredCodes, requiredCodes);
    }

    public List<HistoricTaskInstance> adminTasks() {
        return historyService.createHistoricTaskInstanceQuery().orderByTaskCreateTime().desc().list();
    }


    private TaskDto toTaskDto(Task task) {
        return new TaskDto(
                task.getId(),
                task.getName(),
                task.getAssignee(),
                task.getProcessInstanceId(),
                task.getCreateTime() == null ? null : task.getCreateTime().toInstant(),
                task.getDueDate() == null ? null : task.getDueDate().toInstant(),
                resolveGameProgress(task)
        );
    }

    private TaskGameProgressDto resolveGameProgress(Task task) {
        var processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(task.getProcessDefinitionId())
                .singleResult();
        if (processDefinition == null) {
            return null;
        }

        List<String> requiredCodes = gameLevelCodeRepository
                .findByProcessIdAndLevelKeyOrderByCreatedAtAsc(processDefinition.getId(), task.getTaskDefinitionKey())
                .stream()
                .map(c -> c.getValue().trim())
                .filter(code -> !code.isEmpty())
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(
                                code -> code.toLowerCase(),
                                code -> code,
                                (left, right) -> left,
                                LinkedHashMap::new
                        ),
                        map -> new ArrayList<>(map.values())
                ));

        if (requiredCodes.isEmpty()) {
            return null;
        }

        Set<String> enteredCorrectCodes = gameCodeAttemptRepository.findByTaskIdOrderByCreatedAtAsc(task.getId()).stream()
                .filter(GameCodeAttempt::isCorrect)
                .map(attempt -> attempt.getValue().trim().toLowerCase())
                .collect(Collectors.toSet());

        List<TaskGameCodeDto> codes = requiredCodes.stream()
                .map(code -> new TaskGameCodeDto(code, enteredCorrectCodes.contains(code.toLowerCase())))
                .toList();

        int enteredCorrectCodesCount = (int) codes.stream()
                .filter(TaskGameCodeDto::entered)
                .count();

        return new TaskGameProgressDto(requiredCodes.size(), enteredCorrectCodesCount, codes);
    }
    private Map<String, Object> variablesOrEmpty(CompleteTaskRequest req) {
        return req.variables() == null ? Map.of() : req.variables();
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
