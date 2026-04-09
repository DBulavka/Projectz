package com.example.workflow.service;

import com.example.workflow.dto.group.GroupTypeTelegramBotDto;
import com.example.workflow.entity.GroupTypeTelegramBot;
import com.example.workflow.exception.ApiException;
import com.example.workflow.repository.GroupTypeRepository;
import com.example.workflow.repository.GroupTypeTelegramBotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupTypeTelegramBotService {
    private final GroupTypeRepository groupTypeRepository;
    private final GroupTypeTelegramBotRepository groupTypeTelegramBotRepository;

    @Transactional(readOnly = true)
    public GroupTypeTelegramBotDto get(UUID groupTypeId) {
        ensureGroupTypeExists(groupTypeId);
        boolean configured = groupTypeTelegramBotRepository.findByGroupTypeId(groupTypeId).isPresent();
        return GroupTypeTelegramBotDto.builder()
                .groupTypeId(groupTypeId)
                .configured(configured)
                .build();
    }

    @Transactional
    public GroupTypeTelegramBotDto upsert(UUID groupTypeId, String botToken) {
        ensureGroupTypeExists(groupTypeId);
        String normalized = botToken.trim();
        Instant now = Instant.now();

        groupTypeTelegramBotRepository.findByGroupTypeId(groupTypeId)
                .map(existing -> {
                    existing.setBotToken(normalized);
                    existing.setUpdatedAt(now);
                    return groupTypeTelegramBotRepository.save(existing);
                })
                .orElseGet(() -> groupTypeTelegramBotRepository.save(GroupTypeTelegramBot.builder()
                        .groupTypeId(groupTypeId)
                        .botToken(normalized)
                        .createdAt(now)
                        .updatedAt(now)
                        .build()));

        return GroupTypeTelegramBotDto.builder()
                .groupTypeId(groupTypeId)
                .configured(true)
                .build();
    }

    private void ensureGroupTypeExists(UUID groupTypeId) {
        if (!groupTypeRepository.existsById(groupTypeId)) {
            throw new ApiException("Group type not found");
        }
    }
}
