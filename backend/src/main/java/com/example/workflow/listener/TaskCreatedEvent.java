package com.example.workflow.listener;

import org.flowable.task.service.impl.persistence.entity.TaskEntity;

public record TaskCreatedEvent(TaskEntity task) {
}
