package org.ridgepeak.backend.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AuthRegisterRequest(
        @Pattern(regexp = "^[a-zA-Z0-9_]{3,30}$",
                message = "用户名必须是：3-30位字符，只允许数字、大小写字母、下划线")
        String username,

        @Pattern(regexp = "^[a-zA-Z0-9@#$%^&*]{8,30}$",
                message = "密码必须是：8-30位字符，只允许数字、大小写字母、以及 @ # $ % ^ & * 字符")
        String password,

        @NotBlank
        @Email(message = "无效的电子邮件格式")
        String email) {
}
