package com.example.workflow.dto.auth;

import java.util.UUID;
import com.example.workflow.enums.Role;

public record UserDto(UUID id, String email, Role role) {}
