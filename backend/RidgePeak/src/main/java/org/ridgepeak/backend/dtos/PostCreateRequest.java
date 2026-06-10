package org.ridgepeak.backend.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record PostCreateRequest(
        @NotNull(message = "板块不能为空")
        Long categoryId,

        @NotBlank(message = "帖子标题不能为空")
        @Length(min = 1, max = 100, message = "帖子标题必须是：1-100位字符")
        String title,

        @NotBlank(message = "帖子正文不能为空")
        String content
) {}
