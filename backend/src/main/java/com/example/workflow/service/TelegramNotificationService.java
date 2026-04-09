package com.example.workflow.service;

import com.example.workflow.entity.GroupTypeTelegramBot;
import com.example.workflow.entity.UserGroupTelegramChat;
import com.example.workflow.entity.UserGroup;
import com.example.workflow.repository.GroupTypeTelegramBotRepository;
import com.example.workflow.repository.UserGroupRepository;
import com.example.workflow.repository.UserGroupTelegramChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelegramNotificationService {
    private final UserGroupTelegramChatRepository telegramChatRepository;
    private final UserGroupRepository userGroupRepository;
    private final GroupTypeTelegramBotRepository groupTypeTelegramBotRepository;
    private final Map<String, TelegramClient> clientsByToken = new ConcurrentHashMap<>();

    public void notifyGroup(UUID groupId, String message) {
        notifyGroups(Set.of(groupId), message);
    }

    public void notifyGroups(Collection<UUID> groupIds, String message) {
        if (groupIds == null || groupIds.isEmpty() || message == null || message.isBlank()) {
            return;
        }

        Map<UUID, UserGroup> groupsById = userGroupRepository.findByIdIn(groupIds).stream()
                .collect(Collectors.toMap(UserGroup::getId, group -> group));

        Map<UUID, String> botTokenByGroupTypeId = groupTypeTelegramBotRepository.findByGroupTypeIdIn(
                        groupsById.values().stream().map(UserGroup::getGroupTypeId).collect(Collectors.toSet())
                ).stream()
                .collect(Collectors.toMap(GroupTypeTelegramBot::getGroupTypeId, GroupTypeTelegramBot::getBotToken));

        Map<UUID, Set<String>> chatIdsByGroupId = telegramChatRepository.findByGroupIdIn(groupIds).stream()
                .collect(Collectors.groupingBy(
                        UserGroupTelegramChat::getGroupId,
                        Collectors.mapping(UserGroupTelegramChat::getChatId, Collectors.toCollection(LinkedHashSet::new))
                ));

        for (UUID groupId : groupIds) {
            UserGroup group = groupsById.get(groupId);
            if (group == null) {
                continue;
            }

            String botToken = botTokenByGroupTypeId.get(group.getGroupTypeId());
            if (botToken == null || botToken.isBlank()) {
                log.debug("Telegram bot token is not configured for groupTypeId={}", group.getGroupTypeId());
                continue;
            }

            for (String chatId : chatIdsByGroupId.getOrDefault(groupId, Set.of())) {
                try {
                    TelegramClient client = clientsByToken.computeIfAbsent(botToken, OkHttpTelegramClient::new);
                    client.execute(SendMessage.builder()
                            .chatId(chatId)
                            .text(message)
                            .build());
                } catch (TelegramApiException ex) {
                    log.warn("Failed to send telegram message to chat {} for group {}", chatId, groupId, ex);
                }
            }
        }
    }
}
