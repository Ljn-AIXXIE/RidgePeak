package org.ridgepeak.backend.dtos;

import java.time.LocalDateTime;

public record PostInfo(
        String title,
        String content,
        long viewCount,
        long likeCount,
        CategoryShortInfo category,
        ProfileSummaryInfo author,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
