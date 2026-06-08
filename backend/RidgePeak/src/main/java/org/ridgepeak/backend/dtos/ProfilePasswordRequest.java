package org.ridgepeak.backend.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ProfilePasswordRequest(
        @NotBlank(message = "旧密码不能为空")
        String oldPassword,

        @Pattern(regexp = "^[a-zA-Z0-9@#$%^&*]{8,30}$",
                message = "密码必须是：8-30位字符，只允许数字、大小写字母、以及 @ # $ % ^ & * 字符")
        String newPassword
) {}
