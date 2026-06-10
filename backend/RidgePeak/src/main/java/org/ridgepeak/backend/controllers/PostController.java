package org.ridgepeak.backend.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.ridgepeak.backend.dtos.*;
import org.ridgepeak.backend.services.PostService;
import org.ridgepeak.backend.utils.Result;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/post")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public Result<?> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long category,
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PostSearchResponse searchResponse = postService.search(keyword, category, sort, page, size);
        return Result.ok(searchResponse);
    }

    @GetMapping("/{postId}")
    public Result<?> get(@PathVariable Long postId) {
        PostInfo postInfo = postService.find(postId);
        return Result.ok(postInfo);
    }

    @PostMapping("/{postId}")
    public Result<?> edit(HttpServletRequest request, @PathVariable Long postId, @RequestBody @Valid PostCreateRequest body) {
        Long userId = (Long) request.getAttribute("userId");
        postService.edit(postId, userId, body);
        return Result.ok();
    }

    @DeleteMapping("/{postId}")
    public Result<?> delete(HttpServletRequest request, @PathVariable Long postId) {
        Long userId = (Long) request.getAttribute("userId");
        postService.delete(postId, userId);
        return Result.ok();
    }

    @PostMapping("/create")
    public Result<?> create(HttpServletRequest request, @RequestBody @Valid PostCreateRequest body) {
        Long userId = (Long) request.getAttribute("userId");
        Long postId = postService.create(userId, body);
        return Result.ok(postId);
    }

    @PostMapping("/{postId}/like")
    public Result<?> like(HttpServletRequest request, @PathVariable Long postId) {
        Long userId = (Long) request.getAttribute("userId");
        boolean liked = postService.toggleLike(postId, userId);
        return Result.ok(liked);
    }
}
