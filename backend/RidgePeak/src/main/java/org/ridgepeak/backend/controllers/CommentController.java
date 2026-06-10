package org.ridgepeak.backend.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.ridgepeak.backend.dtos.CommentCreateRequest;
import org.ridgepeak.backend.dtos.CommentInfo;
import org.ridgepeak.backend.services.CommentService;
import org.ridgepeak.backend.utils.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public Result<?> list(@RequestParam Long postId) {
        List<CommentInfo> commentInfoList = commentService.list(postId);
        return Result.ok(commentInfoList);
    }

    @PostMapping("/create")
    public Result<?> create(HttpServletRequest request, @RequestBody @Valid CommentCreateRequest body) {
        Long userId = (Long) request.getAttribute("userId");
        Long commentId = commentService.create(userId, body);
        return Result.ok(commentId);
    }

    @DeleteMapping("/{commentId}")
    public Result<?> delete(HttpServletRequest request, @PathVariable Long commentId) {
        Long userId = (Long) request.getAttribute("userId");
        commentService.delete(commentId, userId);
        return Result.ok();
    }
}
