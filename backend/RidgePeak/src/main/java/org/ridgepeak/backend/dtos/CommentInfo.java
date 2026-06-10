package org.ridgepeak.backend.dtos;

import java.time.LocalDateTime;
import java.util.List;

public record CommentInfo(
        Long commentId,
        String content,
        ProfileSummaryInfo author,
        LocalDateTime createdAt,
        List<CommentInfo> children
) {}
