package com.example.workflow.controller;

import com.example.workflow.dto.auth.LoginRequest;
import com.example.workflow.dto.auth.RegisterRequest;
import com.example.workflow.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public Object register(@Valid @RequestBody RegisterRequest request) { return authService.register(request); }

    @PostMapping("/login")
    public Object login(@Valid @RequestBody LoginRequest request) { return authService.login(request); }

    @GetMapping("/me")
    public Map<String, Object> me() {
        var u = authService.me();
        return Map.of("id", u.getId(), "email", u.getEmail(), "role", u.getRole());
    }
}
