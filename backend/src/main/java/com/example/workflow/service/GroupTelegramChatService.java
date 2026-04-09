package com.example.workflow.service;

import com.example.workflow.dto.group.GroupTelegramChatsDto;
import com.example.workflow.entity.UserGroupTelegramChat;
import com.example.workflow.exception.ApiException;
import com.example.workflow.repository.UserGroupRepository;
import com.example.workflow.repository.UserGroupTelegramChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupTelegramChatService {
    private final UserGroupRepository userGroupRepository;
    private final UserGroupTelegramChatRepository telegramChatRepository;

    @Transactional(readOnly = true)
    public GroupTelegramChatsDto get(UUID groupId) {
        ensureGroupExists(groupId);
        return GroupTelegramChatsDto.builder()
                .groupId(groupId)
                .chatIds(listChatIds(groupId))
                .build();
    }

    @Transactional
    public GroupTelegramChatsDto replace(UUID groupId, List<String> chatIds) {
        ensureGroupExists(groupId);

        List<String> normalizedChatIds = chatIds.stream()
                .map(String::trim)
                .filter(chatId -> !chatId.isBlank())
                .collect(java.util.stream.Collectors.collectingAndThen(
                        java.util.stream.Collectors.toCollection(LinkedHashSet::new),
                        List::copyOf
                ));

        if (normalizedChatIds.isEmpty()) {
            throw new ApiException("At least one telegram chat id is required");
        }

        telegramChatRepository.deleteByGroupId(groupId);
        Instant now = Instant.now();
        telegramChatRepository.saveAll(normalizedChatIds.stream()
                .map(chatId -> UserGroupTelegramChat.builder()
                        .groupId(groupId)
                        .chatId(chatId)
                        .createdAt(now)
                        .build())
                .toList());

        return GroupTelegramChatsDto.builder()
                .groupId(groupId)
                .chatIds(listChatIds(groupId))
                .build();
    }

    private List<String> listChatIds(UUID groupId) {
        return telegramChatRepository.findByGroupId(groupId).stream()
                .sorted(Comparator.comparing(UserGroupTelegramChat::getCreatedAt))
                .map(UserGroupTelegramChat::getChatId)
                .toList();
    }

    private void ensureGroupExists(UUID groupId) {
        if (!userGroupRepository.existsById(groupId)) {
            throw new ApiException("Group not found");
        }
    }
}
