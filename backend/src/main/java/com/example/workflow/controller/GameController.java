package com.example.workflow.controller;

import com.example.workflow.dto.game.*;
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

    @GetMapping("/api/admin/games/{gameId}/instances")
    public List<GameInstanceDto> listInstances(@PathVariable UUID gameId) {
        return gameService.listInstances(gameId);
    }

    @PostMapping("/api/admin/games/start")
    public GameInstanceDto startGame(@RequestBody(required = false) GameStartRequest gameStartRequest) {
        return gameService.startGame(gameStartRequest);
    }

    @PostMapping("/api/games/{gameId}/registrations")
    public GameRegistrationDto register(@PathVariable UUID gameId,
                                        @Valid @RequestBody GameRegistrationRequest req) {
        return gameService.register(gameId, req.getGroupId());
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
