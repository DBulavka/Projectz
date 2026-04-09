package com.example.workflow.listener;

import com.example.workflow.service.TelegramNotificationService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TaskNotificationSubscriber {

    private final RuntimeService runtimeService;
    private final TelegramNotificationService telegramNotificationService;

    public TaskNotificationSubscriber(
            RuntimeService runtimeService,
            TelegramNotificationService telegramNotificationService
    ) {
        this.runtimeService = runtimeService;
        this.telegramNotificationService = telegramNotificationService;
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

        telegramNotificationService.notifyGroup(
                UUID.fromString(processInstance.getBusinessKey()),
                "Новый уровень: " + (task.getName() == null ? task.getTaskDefinitionKey() : task.getName())
        );
    }
}
