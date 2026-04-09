package com.example.workflow.service;

import com.example.workflow.dto.game.GameCreateRequest;
import com.example.workflow.dto.game.GameDto;
import com.example.workflow.dto.game.GameRegistrationDto;
import com.example.workflow.dto.game.GameInstanceDto;
import com.example.workflow.entity.Game;
import com.example.workflow.entity.GameRegistration;
import com.example.workflow.entity.GameInstance;
import com.example.workflow.entity.UserGroup;
import com.example.workflow.enums.GameRegistrationStatus;
import com.example.workflow.enums.GameInstanceStatus;
import com.example.workflow.exception.ApiException;
import com.example.workflow.repository.GameRegistrationRepository;
import com.example.workflow.repository.GameRepository;
import com.example.workflow.repository.GameInstanceRepository;
import com.example.workflow.repository.UserGroupRepository;
import org.flowable.engine.HistoryService;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;
    private final GameRegistrationRepository gameRegistrationRepository;
    private final GameInstanceRepository gameInstanceRepository;
    private final UserGroupRepository userGroupRepository;
    private final RepositoryService repositoryService;
    private final RuntimeService runtimeService;
    private final HistoryService historyService;
    private final TelegramNotificationService telegramNotificationService;

    @Transactional
    public GameDto create(GameCreateRequest req) {
        gameRepository.findByCode(req.getCode())
                .ifPresent(existing -> {
                    throw new ApiException("Game code already exists");
                });

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(req.getProcessDefinitionId())
                .singleResult();

        if (processDefinition == null) {
            throw new ApiException("Process definition not found");
        }

        Instant now = Instant.now();
        Game game = gameRepository.save(Game.builder()
                .code(req.getCode().trim())
                .processDefinitionId(req.getProcessDefinitionId())
                .name(req.getName().trim())
                .description(normalizeNullable(req.getDescription()))
                .startAt(req.getStartAt())
                .createdAt(now)
                .updatedAt(now)
                .build());

        return toDto(game);
    }

    public List<GameDto> list() {
        return gameRepository.findAll().stream().map(this::toDto).toList();
    }

    public List<GameRegistrationDto> listRegistrations(UUID gameId) {
        ensureGameExists(gameId);
        return gameRegistrationRepository.findByGameId(gameId).stream().map(this::toRegistrationDto).toList();
    }

    public List<GameInstanceDto> listInstances(UUID gameId) {
        ensureGameExists(gameId);
        return gameInstanceRepository.findByGameId(gameId).stream().map(this::toInstanceDto).toList();
    }

    @Transactional
    public GameRegistrationDto register(UUID gameId, UUID groupId) {
        Game game = getGame(gameId);
        if (game.getStartedAt() != null) {
            throw new ApiException("Game has already started");
        }

        UserGroup group = userGroupRepository.findById(groupId)
                .orElseThrow(() -> new ApiException("Group not found"));

        Instant now = Instant.now();
        GameRegistration registration = gameRegistrationRepository.findByGameIdAndGroupId(gameId, group.getId())
                .map(existing -> {
                    existing.setStatus(GameRegistrationStatus.PENDING);
                    existing.setUpdatedAt(now);
                    return gameRegistrationRepository.save(existing);
                })
                .orElseGet(() -> gameRegistrationRepository.save(GameRegistration.builder()
                        .gameId(gameId)
                        .groupId(group.getId())
                        .status(GameRegistrationStatus.PENDING)
                        .createdAt(now)
                        .updatedAt(now)
                        .build()));

        upsertGameInstance(gameId, group.getId(), null, GameInstanceStatus.NOT_STARTED, now);

        return toRegistrationDto(registration);
    }

    @Transactional
    public GameRegistrationDto confirmRegistration(UUID registrationId) {
        GameRegistration registration = gameRegistrationRepository.findById(registrationId)
                .orElseThrow(() -> new ApiException("Registration not found"));

        Game game = getGame(registration.getGameId());
        if (game.getStartedAt() != null) {
            throw new ApiException("Cannot confirm registration for already started game");
        }

        registration.setStatus(GameRegistrationStatus.CONFIRMED);
        registration.setUpdatedAt(Instant.now());
        return toRegistrationDto(gameRegistrationRepository.save(registration));
    }

    @Transactional
    public GameRegistrationDto rejectRegistration(UUID registrationId) {
        GameRegistration registration = gameRegistrationRepository.findById(registrationId)
                .orElseThrow(() -> new ApiException("Registration not found"));

        Game game = getGame(registration.getGameId());
        if (game.getStartedAt() != null) {
            throw new ApiException("Cannot reject registration for already started game");
        }

        registration.setStatus(GameRegistrationStatus.REJECTED);
        registration.setUpdatedAt(Instant.now());

        gameInstanceRepository.findByGameIdAndGroupId(registration.getGameId(), registration.getGroupId())
                .ifPresent(gameInstanceRepository::delete);

        return toRegistrationDto(gameRegistrationRepository.save(registration));
    }

    @Scheduled(cron = "${app.games.autostart-cron}")
    @Transactional
    public void autoStartGames() {
        Instant now = Instant.now();
        List<Game> gamesToStart = gameRepository.findByStartedAtIsNullAndStartAtLessThanEqual(now);

        for (Game game : gamesToStart) {
            List<GameRegistration> confirmed = gameRegistrationRepository
                    .findByGameIdAndStatus(game.getId(), GameRegistrationStatus.CONFIRMED);

            for (GameRegistration registration : confirmed) {
                ProcessInstance processInstance = runtimeService.startProcessInstanceById(
                        game.getProcessDefinitionId(),
                        registration.getGroupId().toString()
                );

                upsertGameInstance(game.getId(), registration.getGroupId(), processInstance.getProcessInstanceId(), GameInstanceStatus.IN_PROGRESS, now);
                telegramNotificationService.notifyGroup(
                        registration.getGroupId(),
                        "Игра \"" + game.getName() + "\" началась."
                );
            }

            game.setStartedAt(now);
            game.setUpdatedAt(now);
            gameRepository.save(game);
        }
    }

@Scheduled(cron = "${app.games.status-sync-cron}")
    @Transactional
    public void syncGameInstanceStatuses() {
        List<GameInstance> activeInstances = gameInstanceRepository.findByStatusIn(List.of(GameInstanceStatus.IN_PROGRESS, GameInstanceStatus.PAUSED));
        Instant now = Instant.now();

        for (GameInstance gameInstance : activeInstances) {
            if (gameInstance.getProcessInstanceId() == null || gameInstance.getProcessInstanceId().isBlank()) {
                continue;
            }

            ProcessInstance runtimeInstance = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(gameInstance.getProcessInstanceId())
                    .singleResult();

            if (runtimeInstance != null) {
                gameInstance.setStatus(runtimeInstance.isSuspended() ? GameInstanceStatus.PAUSED : GameInstanceStatus.IN_PROGRESS);
                gameInstance.setUpdatedAt(now);
                gameInstanceRepository.save(gameInstance);
                continue;
            }

            HistoricProcessInstance historicInstance = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(gameInstance.getProcessInstanceId())
                    .singleResult();

            if (historicInstance == null || historicInstance.getEndTime() == null) {
                continue;
            }

            String deleteReason = historicInstance.getDeleteReason();
            gameInstance.setStatus(deleteReason == null ? GameInstanceStatus.COMPLETED : GameInstanceStatus.CANCELLED);
            gameInstance.setUpdatedAt(now);
            gameInstanceRepository.save(gameInstance);

            gameRepository.findById(gameInstance.getGameId())
                    .ifPresent(game -> telegramNotificationService.notifyGroup(
                            gameInstance.getGroupId(),
                            "Игра \"" + game.getName() + "\" завершена со статусом: " + gameInstance.getStatus().name()
                    ));
        }
    }

    private void upsertGameInstance(UUID gameId, UUID groupId, String processInstanceId, GameInstanceStatus status, Instant now) {
        gameInstanceRepository.findByGameIdAndGroupId(gameId, groupId)
                .map(existing -> {
                    existing.setProcessInstanceId(processInstanceId);
                    existing.setStatus(status);
                    existing.setUpdatedAt(now);
                    return gameInstanceRepository.save(existing);
                })
                .orElseGet(() -> gameInstanceRepository.save(GameInstance.builder()
                        .gameId(gameId)
                        .groupId(groupId)
                        .processInstanceId(processInstanceId)
                        .status(status)
                        .createdAt(now)
                        .updatedAt(now)
                        .build()));
    }

    private Game getGame(UUID gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new ApiException("Game not found"));
    }

    private void ensureGameExists(UUID gameId) {
        if (!gameRepository.existsById(gameId)) {
            throw new ApiException("Game not found");
        }
    }

    private GameDto toDto(Game game) {
        return GameDto.builder()
                .id(game.getId())
                .code(game.getCode())
                .processDefinitionId(game.getProcessDefinitionId())
                .name(game.getName())
                .description(game.getDescription())
                .startAt(game.getStartAt())
                .startedAt(game.getStartedAt())
                .build();
    }

    private GameRegistrationDto toRegistrationDto(GameRegistration registration) {
        return GameRegistrationDto.builder()
                .id(registration.getId())
                .gameId(registration.getGameId())
                .groupId(registration.getGroupId())
                .status(registration.getStatus())
                .build();
    }

    private GameInstanceDto toInstanceDto(GameInstance gameInstance) {
        return GameInstanceDto.builder()
                .id(gameInstance.getId())
                .gameId(gameInstance.getGameId())
                .groupId(gameInstance.getGroupId())
                .processInstanceId(gameInstance.getProcessInstanceId())
                .status(gameInstance.getStatus())
                .build();
    }

    private String normalizeNullable(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}
