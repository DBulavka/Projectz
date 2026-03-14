package com.example.workflow.service;

import com.example.workflow.dto.game.GameCreateRequest;
import com.example.workflow.dto.game.GameDto;
import com.example.workflow.dto.game.GameRegistrationDto;
import com.example.workflow.entity.Game;
import com.example.workflow.entity.GameRegistration;
import com.example.workflow.entity.UserGroup;
import com.example.workflow.enums.GameRegistrationStatus;
import com.example.workflow.exception.ApiException;
import com.example.workflow.repository.GameRegistrationRepository;
import com.example.workflow.repository.GameRepository;
import com.example.workflow.repository.UserGroupRepository;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.RepositoryService;
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
    private final UserGroupRepository userGroupRepository;
    private final RepositoryService repositoryService;
    private final RuntimeService runtimeService;

    @Transactional
    public GameDto create(GameCreateRequest req) {
        gameRepository.findByNumber(req.number())
                .ifPresent(existing -> {
                    throw new ApiException("Game number already exists");
                });

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(req.processDefinitionId())
                .singleResult();

        if (processDefinition == null) {
            throw new ApiException("Process definition not found");
        }

        Instant now = Instant.now();
        Game game = gameRepository.save(Game.builder()
                .number(req.number())
                .processDefinitionId(req.processDefinitionId())
                .name(req.name().trim())
                .description(normalizeNullable(req.description()))
                .startAt(req.startAt())
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

    @Scheduled(fixedDelayString = "${app.games.autostart-delay-ms:10000}")
    @Transactional
    public void autoStartGames() {
        Instant now = Instant.now();
        List<Game> gamesToStart = gameRepository.findByStartedAtIsNullAndStartAtLessThanEqual(now);

        for (Game game : gamesToStart) {
            List<GameRegistration> confirmed = gameRegistrationRepository
                    .findByGameIdAndStatus(game.getId(), GameRegistrationStatus.CONFIRMED);

            for (GameRegistration registration : confirmed) {
                runtimeService.startProcessInstanceById(
                        game.getProcessDefinitionId(),
                        registration.getGroupId().toString()
                );
            }

            game.setStartedAt(now);
            game.setUpdatedAt(now);
            gameRepository.save(game);
        }
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
        return new GameDto(
                game.getId(),
                game.getNumber(),
                game.getProcessDefinitionId(),
                game.getName(),
                game.getDescription(),
                game.getStartAt(),
                game.getStartedAt()
        );
    }

    private GameRegistrationDto toRegistrationDto(GameRegistration registration) {
        return new GameRegistrationDto(
                registration.getId(),
                registration.getGameId(),
                registration.getGroupId(),
                registration.getStatus()
        );
    }

    private String normalizeNullable(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}
