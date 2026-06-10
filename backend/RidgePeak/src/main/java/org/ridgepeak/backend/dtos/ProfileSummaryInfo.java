package org.ridgepeak.backend.dtos;

import org.ridgepeak.backend.models.Role;

public record ProfileSummaryInfo(
        Long userId,
        String username,
        String nickname,
        String avatarUrl,
        Role role
) {}
