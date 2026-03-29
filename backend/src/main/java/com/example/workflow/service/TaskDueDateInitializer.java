package com.example.workflow.service;

import org.flowable.bpmn.model.BoundaryEvent;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.EventDefinition;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.TimerEventDefinition;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEntityEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.engine.RepositoryService;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Configuration
public class TaskDueDateInitializer {

    @Bean
    public EngineConfigurationConfigurer<SpringProcessEngineConfiguration> dueDateTaskListenerConfigurer() {
        return configuration -> configuration.getEventDispatcher().addEventListener(new FlowableEventListener() {
            @Override
            public void onEvent(FlowableEvent event) {
                if (event.getType() != FlowableEngineEventType.TASK_CREATED) {
                    return;
                }
                if (!(event instanceof FlowableEntityEvent entityEvent) || !(entityEvent.getEntity() instanceof TaskEntity task)) {
                    return;
                }

                Optional<Instant> dueDate = resolveDueDate(task, configuration.getRepositoryService());
                dueDate.ifPresent(instant -> configuration.getTaskService().setDueDate(task.getId(), Date.from(instant)));
            }

            @Override
            public boolean isFailOnException() {
                return false;
            }

            @Override
            public boolean isFireOnTransactionLifecycleEvent() {
                return false;
            }

            @Override
            public String getOnTransaction() {
                return null;
            }
        });
    }

    private Optional<Instant> resolveDueDate(TaskEntity task, RepositoryService repositoryService) {
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
