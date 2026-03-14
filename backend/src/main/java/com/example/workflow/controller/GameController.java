package com.example.workflow.controller;

import com.example.workflow.dto.game.GameCreateRequest;
import com.example.workflow.dto.game.GameDto;
import com.example.workflow.dto.game.GameRegistrationDto;
import com.example.workflow.dto.game.GameRegistrationRequest;
import com.example.workflow.dto.game.GameTeamDto;
import com.example.workflow.service.GameService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GameController {
    private final GameService gameService;

    @GetMapping("/api/games")
    public List<GameDto> list() {
        return gameService.list();
    }

    @PostMapping("/api/admin/games")
    public GameDto create(@Valid @RequestBody GameCreateRequest req) {
        return gameService.create(req);
    }

    @GetMapping("/api/admin/games/{gameId}/registrations")
    public List<GameRegistrationDto> listRegistrations(@PathVariable UUID gameId) {
        return gameService.listRegistrations(gameId);
    }

    @GetMapping("/api/admin/games/{gameId}/teams")
    public List<GameTeamDto> listTeams(@PathVariable UUID gameId) {
        return gameService.listTeams(gameId);
    }

    @PostMapping("/api/games/{gameId}/registrations")
    public GameRegistrationDto register(@PathVariable UUID gameId,
                                        @Valid @RequestBody GameRegistrationRequest req) {
        return gameService.register(gameId, req.groupId());
    }

    @PostMapping("/api/admin/game-registrations/{registrationId}/confirm")
    public GameRegistrationDto confirm(@PathVariable UUID registrationId) {
        return gameService.confirmRegistration(registrationId);
    }

    @PostMapping("/api/admin/game-registrations/{registrationId}/reject")
    public GameRegistrationDto reject(@PathVariable UUID registrationId) {
        return gameService.rejectRegistration(registrationId);
    }
}
