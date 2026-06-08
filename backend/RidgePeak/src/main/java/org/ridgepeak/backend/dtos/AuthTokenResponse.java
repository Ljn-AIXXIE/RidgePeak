package org.ridgepeak.backend.dtos;

public record AuthTokenResponse(String accessToken, String tokenType, int expiresIn) {
    public AuthTokenResponse(String accessToken) {
        this(accessToken, "Bearer", 21600);
    }
}
