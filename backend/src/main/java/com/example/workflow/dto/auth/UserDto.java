package com.example.workflow.dto.auth;

import com.example.workflow.enums.Role;

public record UserDto(Long id, String email, Role role) {}
