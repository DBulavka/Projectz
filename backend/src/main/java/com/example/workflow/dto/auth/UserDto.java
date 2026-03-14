package com.example.workflow.dto.auth;

import com.example.workflow.enums.Role;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserDto {
    UUID id;
    String email;
    Role role;
}
