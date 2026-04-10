package com.example.workflow.service;

import java.util.Collection;
import java.util.UUID;

public interface NotificationSender {
    void notifyGroup(UUID groupId, String message);

    void notifyGroups(Collection<UUID> groupIds, String message);
}
