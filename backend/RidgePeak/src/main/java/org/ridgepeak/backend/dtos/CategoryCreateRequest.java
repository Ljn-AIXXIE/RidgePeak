package org.ridgepeak.backend.dtos;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record CategoryCreateRequest(
        @NotBlank(message = "板块名不能为空")
        @Length(min = 1, max = 20, message = "板块名必须是：1-20位字符")
        String name,

        @Length(min = 1, max = 50, message = "板块描述必须是：1-50位字符")
        String description
) {}
