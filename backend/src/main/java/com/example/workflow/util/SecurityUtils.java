package com.example.workflow.util;

import java.util.UUID;
import com.example.workflow.exception.ApiException;
import com.example.workflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtils {
    private final UserRepository userRepository;

    public String currentEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) throw new ApiException("Unauthenticated");
        return auth.getName();
    }

    public UUID currentUserId() {
        return userRepository.findByEmail(currentEmail()).orElseThrow(() -> new ApiException("User not found")).getId();
    }

    public boolean isAdmin() {
        return userRepository.findByEmail(currentEmail()).orElseThrow(() -> new ApiException("User not found")).getRole().name().equals("ADMIN");
    }
}
