package com.example.workflow.controller;

import com.example.workflow.dto.auth.AuthResponse;
import com.example.workflow.dto.auth.LoginRequest;
import com.example.workflow.dto.auth.RegisterRequest;
import com.example.workflow.dto.auth.UserDto;
import com.example.workflow.mapper.UserMapper;
import com.example.workflow.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserMapper userMapper;

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public UserDto me() {
        return userMapper.toDto(authService.me());
    }
}
