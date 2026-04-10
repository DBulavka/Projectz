package com.example.workflow.service;

import com.example.workflow.enums.GameInstanceStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationSender notificationSender;

    public void notifyGameStarted(UUID groupId, String gameName) {
        notificationSender.notifyGroup(groupId, "Игра \"" + gameName + "\" началась.");
    }

    public void notifyNewLevel(UUID groupId, String levelName) {
        notificationSender.notifyGroup(groupId, "Новый уровень: " + levelName);
    }

    public void notifyCurrentLevelStatus(UUID groupId, String levelName, String status) {
        notificationSender.notifyGroup(groupId, "Статус уровня \"" + levelName + "\": " + status);
    }

    public void notifyCodeInputResult(UUID groupId, String levelName, boolean success, String details) {
        String result = success ? "успешно" : "ошибка";
        String suffix = (details == null || details.isBlank()) ? "" : ". " + details;
        notificationSender.notifyGroup(groupId, "Результат ввода кода на уровне \"" + levelName + "\": " + result + suffix);
    }

    public void notifyLevelCompleted(UUID groupId, String levelName) {
        notificationSender.notifyGroup(groupId, "Уровень \"" + levelName + "\" завершён.");
    }

    public void notifyGameCompleted(UUID groupId, String gameName, GameInstanceStatus status) {
        notificationSender.notifyGroup(groupId, "Игра \"" + gameName + "\" завершена со статусом: " + status.name());
    }

    public void notifyCustom(UUID groupId, String message) {
        notificationSender.notifyGroup(groupId, message);
    }
}
