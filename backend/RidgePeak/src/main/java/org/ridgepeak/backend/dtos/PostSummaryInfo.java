package org.ridgepeak.backend.dtos;

import java.time.LocalDateTime;

public record PostSummaryInfo(
        Long postId,
        String title,
        String categoryName,
        String authorName,
        long viewCount,
        LocalDateTime createdAt
) {}