package org.ridgepeak.backend.dtos;

import java.util.List;

public record PostSearchResponse(
        List<PostSummaryInfo> posts,
        long postCount,
        int currentPage,
        int totalPages
) {}
