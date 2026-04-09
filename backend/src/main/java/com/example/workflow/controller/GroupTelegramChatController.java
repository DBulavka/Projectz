package com.example.workflow.controller;

import com.example.workflow.dto.group.GroupTelegramChatsDto;
import com.example.workflow.dto.group.GroupTelegramChatsRequest;
import com.example.workflow.dto.group.GroupTypeTelegramBotDto;
import com.example.workflow.dto.group.GroupTypeTelegramBotRequest;
import com.example.workflow.service.GroupTelegramChatService;
import com.example.workflow.service.GroupTypeTelegramBotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GroupTelegramChatController {
    private final GroupTelegramChatService groupTelegramChatService;
    private final GroupTypeTelegramBotService groupTypeTelegramBotService;

    @GetMapping("/api/admin/groups/{groupId}/telegram-chats")
    public GroupTelegramChatsDto get(@PathVariable UUID groupId) {
        return groupTelegramChatService.get(groupId);
    }

    @PutMapping("/api/admin/groups/{groupId}/telegram-chats")
    public GroupTelegramChatsDto replace(@PathVariable UUID groupId,
                                         @Valid @RequestBody GroupTelegramChatsRequest req) {
        return groupTelegramChatService.replace(groupId, req.getChatIds());
    }

    @GetMapping("/api/admin/group-types/{groupTypeId}/telegram-bot")
    public GroupTypeTelegramBotDto getBot(@PathVariable UUID groupTypeId) {
        return groupTypeTelegramBotService.get(groupTypeId);
    }

    @PutMapping("/api/admin/group-types/{groupTypeId}/telegram-bot")
    public GroupTypeTelegramBotDto upsertBot(@PathVariable UUID groupTypeId,
                                             @Valid @RequestBody GroupTypeTelegramBotRequest req) {
        return groupTypeTelegramBotService.upsert(groupTypeId, req.getBotToken());
    }
}
