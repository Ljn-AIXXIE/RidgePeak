package org.ridgepeak.backend.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record ProfilePutRequest(
//        @NotBlank
//        @Email(message = "无效的电子邮件格式")
//        String email,

        @NotBlank(message = "昵称不能为空")
        @Length(min = 1, max = 30, message = "昵称必须是：1-30位字符")
        String nickname
) {}
