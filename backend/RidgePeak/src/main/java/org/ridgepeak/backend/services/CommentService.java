package org.ridgepeak.backend.services;

import org.ridgepeak.backend.dtos.CommentCreateRequest;
import org.ridgepeak.backend.dtos.CommentInfo;
import org.ridgepeak.backend.dtos.ProfileSummaryInfo;
import org.ridgepeak.backend.exceptions.BizException;
import org.ridgepeak.backend.exceptions.ForbiddenException;
import org.ridgepeak.backend.models.Comment;
import org.ridgepeak.backend.models.Post;
import org.ridgepeak.backend.models.Role;
import org.ridgepeak.backend.models.User;
import org.ridgepeak.backend.repositories.CommentRepository;
import org.ridgepeak.backend.repositories.PostRepository;
import org.ridgepeak.backend.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public CommentService(
            CommentRepository commentRepository,
            PostRepository postRepository,
            UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public List<CommentInfo> list(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BizException("帖子不存在"));

        List<Comment> allComments = commentRepository.findByPostOrderByCreatedAtAsc(post);

        Map<Long, List<Comment>> childrenMap = new HashMap<>();
        List<Comment> roots = new ArrayList<>();

        for (Comment c : allComments) {
            if (c.getParent() == null) {
                roots.add(c);
            } else {
                childrenMap
                        .computeIfAbsent(c.getParent().getId(), k -> new ArrayList<>())
                        .add(c);
            }
        }

        return roots.stream()
                .map(r -> buildTree(r, childrenMap))
                .toList();
    }

    public Long create(Long userId, CommentCreateRequest request) {
        Post post = postRepository.findById(request.postId())
                .orElseThrow(() -> new BizException("帖子不存在"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BizException("用户不存在"));

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setContent(request.content());
        comment.setAuthor(user);

        if (request.parentId() != null) {
            Comment parent = commentRepository.findById(request.parentId())
                    .orElseThrow(() -> new BizException("评论不存在"));
            comment.setParent(parent);
        }

        commentRepository.save(comment);
        return comment.getId();
    }

    public void delete(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BizException("评论不存在"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BizException("用户不存在"));

        User author = comment.getAuthor();
        if (user.getRole() != Role.ADMIN && !userId.equals(author.getId()))
            throw new ForbiddenException("无权操作");

        commentRepository.delete(comment);
    }

    private CommentInfo buildTree(Comment comment, Map<Long, List<Comment>> childrenMap) {
        List<CommentInfo> children = childrenMap
                .getOrDefault(comment.getId(), List.of())
                .stream()
                .map(c -> buildTree(c, childrenMap))
                .toList();

        return new CommentInfo(
                comment.getId(),
                comment.getContent(),
                new ProfileSummaryInfo(
                        comment.getAuthor().getId(),
                        comment.getAuthor().getUsername(),
                        comment.getAuthor().getNickname(),
                        comment.getAuthor().getAvatarUrl(),
                        comment.getAuthor().getRole()
                ),
                comment.getCreatedAt(),
                children
        );
    }
}
