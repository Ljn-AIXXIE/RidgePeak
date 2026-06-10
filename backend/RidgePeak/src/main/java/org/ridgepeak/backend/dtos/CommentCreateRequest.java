package org.ridgepeak.backend.dtos;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record CommentCreateRequest(
        @NotNull(message = "帖子不能为空")
        Long postId,

        @NotBlank(message = "评论内容不能为空")
        @Length(min = 1, max = 300, message = "评论内容必须是：1-300位字符")
        String content,

        @Nullable
        Long parentId
) {}
