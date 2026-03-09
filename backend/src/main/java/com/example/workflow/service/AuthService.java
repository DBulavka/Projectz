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
        if (userRepository.existsByEmail(request.email())) throw new ApiException("Email already exists");
        User user = userRepository.save(User.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .createdAt(Instant.now())
                .build());
        return new AuthResponse(jwtService.generateToken(user.getEmail(), Map.of("role", user.getRole().name())));
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        User user = userRepository.findByEmail(request.email()).orElseThrow(() -> new ApiException("User not found"));
        return new AuthResponse(jwtService.generateToken(user.getEmail(), Map.of("role", user.getRole().name())));
    }

    public User me() {
        return userRepository.findByEmail(securityUtils.currentEmail()).orElseThrow(() -> new ApiException("User not found"));
    }
}
