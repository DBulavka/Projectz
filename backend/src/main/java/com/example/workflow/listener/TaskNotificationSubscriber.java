package com.example.workflow.listener;

import com.example.workflow.service.NotificationService;
import com.example.workflow.dto.task.TaskGameCodeDto;
import com.example.workflow.repository.GameLevelCodeRepository;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class TaskNotificationSubscriber {

    private final RuntimeService runtimeService;
    private final NotificationService notificationService;
    private final GameLevelCodeRepository gameLevelCodeRepository;

    public TaskNotificationSubscriber(
            RuntimeService runtimeService,
            NotificationService notificationService,
            GameLevelCodeRepository gameLevelCodeRepository
    ) {
        this.runtimeService = runtimeService;
        this.notificationService = notificationService;
        this.gameLevelCodeRepository = gameLevelCodeRepository;
    }

    @EventListener
    public void onTaskCreated(TaskCreatedEvent event) {
        TaskEntity task = event.task();
        if (task.getProcessInstanceId() == null || task.getProcessInstanceId().isBlank()) {
            return;
        }

        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(task.getProcessInstanceId())
                .singleResult();

        if (processInstance == null || processInstance.getBusinessKey() == null || processInstance.getBusinessKey().isBlank()) {
            return;
        }

        List<TaskGameCodeDto> codes = gameLevelCodeRepository
                .findByProcessIdAndLevelKeyOrderByCreatedAtAsc(task.getProcessDefinitionId(), task.getTaskDefinitionKey())
                .stream()
                .map(code -> TaskGameCodeDto.builder()
                        .code(code.getCode())
                        .difficulty(code.getDifficulty().getValue())
                        .description(code.getDescription())
                        .value(null)
                        .done(false)
                        .build())
                .toList();

        notificationService.notifyNewLevel(
                UUID.fromString(processInstance.getBusinessKey()),
                task.getName() == null ? task.getTaskDefinitionKey() : task.getName(),
                codes
        );
    }
}
