package org.ridgepeak.backend.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.ridgepeak.backend.services.AuthService;
import org.ridgepeak.backend.utils.UnauthorizedException;
import org.ridgepeak.backend.utils.JwtUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TokenAuthHandler implements HandlerInterceptor {
    private final AuthService authService;

    public TokenAuthHandler(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer "))
            throw new UnauthorizedException("未登录或验证失败");

        var claims = JwtUtil.parse(header.substring(7));
        Long userId = JwtUtil.getUserId(claims);
        String tokenIdentifier = JwtUtil.getTokenIdentifier(claims);
        authService.validate(userId, tokenIdentifier);

        request.setAttribute("userId", userId);
        return true;
    }
}
