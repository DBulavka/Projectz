package com.example.workflow.listener;

import com.example.workflow.service.NotificationService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TaskNotificationSubscriber {

    private final RuntimeService runtimeService;
    private final NotificationService notificationService;

    public TaskNotificationSubscriber(
            RuntimeService runtimeService,
            NotificationService notificationService
    ) {
        this.runtimeService = runtimeService;
        this.notificationService = notificationService;
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

        notificationService.notifyNewLevel(
                UUID.fromString(processInstance.getBusinessKey()),
                task.getName() == null ? task.getTaskDefinitionKey() : task.getName()
        );
    }
}
