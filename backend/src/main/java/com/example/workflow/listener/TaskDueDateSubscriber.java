package com.example.workflow.listener;

import org.flowable.bpmn.model.*;
import org.flowable.bpmn.model.Process;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.TaskService;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.*;

@Component
public class TaskDueDateSubscriber {

    private final RepositoryService repositoryService;
    private final TaskService taskService;

    public TaskDueDateSubscriber(RepositoryService repositoryService, TaskService taskService) {
        this.repositoryService = repositoryService;
        this.taskService = taskService;
    }

    @EventListener
    public void onTaskCreated(TaskCreatedEvent event) {
        TaskEntity task = event.task();
        resolveDueDate(task).ifPresent(instant -> taskService.setDueDate(task.getId(), Date.from(instant)));
    }

    private Optional<Instant> resolveDueDate(TaskEntity task) {
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        if (bpmnModel == null || bpmnModel.getMainProcess() == null) {
            return Optional.empty();
        }

        Process process = bpmnModel.getMainProcess();
        Instant baseInstant = task.getCreateTime() == null ? Instant.now() : task.getCreateTime().toInstant();
        List<Instant> boundaryEventInstants = new ArrayList<>();

        for (FlowElement flowElement : process.getFlowElements()) {
            if (!(flowElement instanceof BoundaryEvent boundaryEvent)) {
                continue;
            }
            if (!task.getTaskDefinitionKey().equals(boundaryEvent.getAttachedToRefId())) {
                continue;
            }

            for (EventDefinition eventDefinition : boundaryEvent.getEventDefinitions()) {
                if (!(eventDefinition instanceof TimerEventDefinition timerEventDefinition)) {
                    continue;
                }
                parseTimerInstant(timerEventDefinition, baseInstant).ifPresent(boundaryEventInstants::add);
            }
        }

        return boundaryEventInstants.stream().min(Comparator.naturalOrder());
    }

    private Optional<Instant> parseTimerInstant(TimerEventDefinition timerEventDefinition, Instant baseInstant) {
        String timeDuration = timerEventDefinition.getTimeDuration();
        if (timeDuration != null && !timeDuration.isBlank()) {
            return parseDuration(timeDuration.trim()).map(baseInstant::plus);
        }

        String timeDate = timerEventDefinition.getTimeDate();
        if (timeDate != null && !timeDate.isBlank()) {
            return parseDate(timeDate.trim());
        }

        String timeCycle = timerEventDefinition.getTimeCycle();
        if (timeCycle != null && !timeCycle.isBlank()) {
            return parseTimeCycle(timeCycle.trim(), baseInstant);
        }

        return Optional.empty();
    }

    private Optional<Duration> parseDuration(String value) {
        try {
            return Optional.of(Duration.parse(value));
        } catch (DateTimeParseException ex) {
            return Optional.empty();
        }
    }

    private Optional<Instant> parseDate(String value) {
        try {
            return Optional.of(Instant.parse(value));
        } catch (DateTimeParseException ignored) {
            try {
                return Optional.of(OffsetDateTime.parse(value).toInstant());
            } catch (DateTimeParseException ignoredAgain) {
                try {
                    return Optional.of(ZonedDateTime.parse(value).toInstant());
                } catch (DateTimeException ex) {
                    return Optional.empty();
                }
            }
        }
    }

    private Optional<Instant> parseTimeCycle(String value, Instant baseInstant) {
        if (!value.startsWith("R")) {
            return parseDuration(value).map(baseInstant::plus);
        }

        String[] parts = value.split("/");
        if (parts.length == 2) {
            return parseDuration(parts[1]).map(baseInstant::plus);
        }

        if (parts.length == 3) {
            Optional<Instant> start = parseDate(parts[1]);
            Optional<Duration> period = parseDuration(parts[2]);
            if (start.isEmpty() || period.isEmpty()) {
                return Optional.empty();
            }

            Instant candidate = start.get();
            Duration step = period.get();
            if (step.isZero() || step.isNegative()) {
                return Optional.empty();
            }
            while (!candidate.isAfter(baseInstant)) {
                candidate = candidate.plus(step);
            }
            return Optional.of(candidate);
        }

        return Optional.empty();
    }
}
