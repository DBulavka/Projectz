package com.example.workflow.config.listener;

import com.example.workflow.listener.TaskCreatedEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEntityEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Configuration
public class TaskCreatedListenerConfig {

    @Bean
    public EngineConfigurationConfigurer<SpringProcessEngineConfiguration> taskCreatedListenerConfigurer(
            ApplicationEventPublisher eventPublisher
    ) {
        return configuration -> {
            List<FlowableEventListener> listeners = new ArrayList<>(
                    Optional.ofNullable(configuration.getEventListeners()).orElseGet(List::of)
            );

            listeners.add(new FlowableEventListener() {
                @Override
                public void onEvent(FlowableEvent event) {
                    if (event.getType() != FlowableEngineEventType.TASK_CREATED) {
                        return;
                    }
                    if (!(event instanceof FlowableEntityEvent entityEvent)
                            || !(entityEvent.getEntity() instanceof TaskEntity task)) {
                        return;
                    }

                    eventPublisher.publishEvent(new TaskCreatedEvent(task));
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

            configuration.setEventListeners(listeners);
        };
    }
}
