package com.example.workflow.service;

import com.example.workflow.dto.auth.AuthResponse;
import com.example.workflow.dto.auth.LoginRequest;
import com.example.workflow.dto.auth.RegisterRequest;
import com.example.workflow.entity.User;
import com.example.workflow.enums.Role;
import com.example.workflow.exception.ApiException;
import com.example.workflow.repository.UserRepository;
import com.example.workflow.security.JwtService;
import com.example.workflow.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final SecurityUtils securityUtils;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) throw new ApiException("Email already exists");
        User user = userRepository.save(User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .createdAt(Instant.now())
                .build());
        return AuthResponse.builder()
                .token(jwtService.generateToken(user.getEmail(), Map.of("role", user.getRole().name())))
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new ApiException("User not found"));
        return AuthResponse.builder()
                .token(jwtService.generateToken(user.getEmail(), Map.of("role", user.getRole().name())))
                .build();
    }

    public User me() {
        return userRepository.findByEmail(securityUtils.currentEmail()).orElseThrow(() -> new ApiException("User not found"));
    }
}
