package org.ridgepeak.backend.dtos;

import org.ridgepeak.backend.models.Role;

import java.time.LocalDateTime;

public record ProfileInfo(
        Long userId,
        String username,
        String email,
        String nickname,
        String avatarUrl,
        Role role,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime lastLoginTime
) {}
