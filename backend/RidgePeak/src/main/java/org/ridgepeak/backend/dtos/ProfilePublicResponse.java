package org.ridgepeak.backend.dtos;

import org.ridgepeak.backend.models.Role;

public record ProfilePublicResponse(
        String username,
        String nickname,
        String avatarUrl,
        Role role
) {}
