package com.example.workflow.dto.auth;

import java.util.UUID;
import com.example.workflow.enums.Role;

@lombok.Value
@lombok.Builder
public class UserDto {
    private UUID id;
    private String email;
    private Role role;
}
