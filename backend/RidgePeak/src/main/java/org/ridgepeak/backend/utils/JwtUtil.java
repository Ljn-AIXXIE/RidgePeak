package org.ridgepeak.backend.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtUtil {
    private JwtUtil() {}

    private static final String SECRET = "73EEC309-0BE7-4A58-BE29-84307F3F56CD";
    private static final long EXPIRATION = 21600;

    private static final SecretKey KEY;

    static {
        KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    public static String generate(Long userId, String tokenIdentifier) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + EXPIRATION * 1000);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("token_identifier", tokenIdentifier)
                .issuedAt(now)
                .notBefore(now)
                .expiration(exp)
                .signWith(KEY)
                .compact();
    }

    public static Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public static Long getUserId(Claims claims) {
        return Long.parseLong(claims.getSubject());
    }

    public static String getTokenIdentifier(Claims claims) {
        return claims.get("token_identifier", String.class);
    }
}
