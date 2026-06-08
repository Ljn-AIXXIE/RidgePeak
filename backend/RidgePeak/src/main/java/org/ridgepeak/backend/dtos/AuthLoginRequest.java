package org.ridgepeak.backend.dtos;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;

public record AuthLoginRequest(
        @JsonAlias({"email", "username"})
        @NotBlank(message = "用户名/邮箱不能为空")
        String account,

        @NotBlank(message = "密码不能为空")
        String password) {
}