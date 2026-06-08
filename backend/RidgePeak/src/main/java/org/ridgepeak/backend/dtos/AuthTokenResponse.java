package org.ridgepeak.backend.dtos;

import org.ridgepeak.backend.utils.JwtUtil;

public record AuthTokenResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {
    public AuthTokenResponse(String accessToken) {
        this(accessToken, "Bearer", JwtUtil.EXPIRATION);
    }
}
