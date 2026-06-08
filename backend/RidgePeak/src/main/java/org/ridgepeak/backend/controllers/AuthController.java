package org.ridgepeak.backend.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.ridgepeak.backend.dtos.AuthLoginRequest;
import org.ridgepeak.backend.dtos.AuthTokenResponse;
import org.ridgepeak.backend.dtos.AuthRegisterRequest;
import org.ridgepeak.backend.services.AuthService;
import org.ridgepeak.backend.utils.JwtUtil;
import org.ridgepeak.backend.utils.Result;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public Result<?> register(@RequestBody @Valid AuthRegisterRequest request) {
        String accessToken = authService.register(request);
        return Result.ok(new AuthTokenResponse(accessToken));
    }

    @PostMapping("/login")
    public Result<?> login(@RequestBody @Valid AuthLoginRequest request) {
        String accessToken = authService.login(request);
        return Result.ok(new AuthTokenResponse(accessToken));
    }

    @GetMapping("/logout")
    public Result<?> logout(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        authService.logout(userId);
        return Result.ok();
    }

    @GetMapping("/validate")
    public Result<Boolean> validate(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return Result.ok(false);

        try {
            String token = authHeader.substring(7);
            var claims = JwtUtil.parse(token);
            authService.validate(
                    JwtUtil.getUserId(claims),
                    JwtUtil.getTokenIdentifier(claims)
            );
            return Result.ok(true);
        } catch (Exception e) {
            return Result.ok(false);
        }
    }
}
