package org.ridgepeak.backend.services;

import org.ridgepeak.backend.dtos.*;
import org.ridgepeak.backend.exceptions.BizException;
import org.ridgepeak.backend.exceptions.ForbiddenException;
import org.ridgepeak.backend.models.*;
import org.ridgepeak.backend.repositories.CategoryRepository;
import org.ridgepeak.backend.repositories.PostLikeRepository;
import org.ridgepeak.backend.repositories.PostRepository;
import org.ridgepeak.backend.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PostService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final PostLikeRepository postLikeRepository;

    public PostService(
            UserRepository userRepository,
            PostRepository postRepository,
            CategoryRepository categoryRepository,
            PostLikeRepository postLikeRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.categoryRepository = categoryRepository;
        this.postLikeRepository = postLikeRepository;
    }

    public PostSearchResponse search(String keyword, Long categoryId, String sort, int page, int size) {
        Sort sortObj = switch (sort) {
            case "popular" -> Sort.by(Sort.Direction.DESC, "viewCount");
            default       -> Sort.by(Sort.Direction.DESC, "createdAt");
        };
        Pageable pageable = PageRequest.of(page, size, sortObj);

        Page<Post> pagedResult;
        if (keyword != null && categoryId != null) {
            Category cat = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new BizException("板块不存在"));
            pagedResult = postRepository.findByTitleContainingAndCategory(keyword, cat, pageable);
        } else if (keyword != null) {
            pagedResult = postRepository.findByTitleContaining(keyword, pageable);
        } else if (categoryId != null) {
            Category cat = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new BizException("板块不存在"));
            pagedResult = postRepository.findByCategory(cat, pageable);
        } else {
            pagedResult = postRepository.findAll(pageable);
        }

        List<PostSummaryInfo> summaries = pagedResult
                .getContent()
                .stream()
                .map(this::extractPostSummaryInfo)
            .toList();

        return new PostSearchResponse(
                summaries,
                pagedResult.getTotalElements(),
                pagedResult.getNumber(),
                pagedResult.getTotalPages()
        );
    }

    public PostInfo find(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BizException("帖子不存在"));

        User author = post.getAuthor();
        Category category = post.getCategory();

        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post);

        return new PostInfo(
                post.getTitle(),
                post.getContent(),
                post.getViewCount(),
                postLikeRepository.countByPost(post),
                new CategoryShortInfo(
                        category.getId(),
                        category.getName()
                ),
                new ProfileSummaryInfo(
                        author.getId(),
                        author.getUsername(),
                        author.getNickname(),
                        author.getAvatarUrl(),
                        author.getRole()
                ),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }

    public Long create(Long userId, PostCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BizException("用户不存在"));
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new BizException("板块不存在"));

        Post post = new Post();
        post.setAuthor(user);
        post.setCategory(category);
        post.setTitle(request.title());
        post.setContent(request.content());
        postRepository.save(post);

        return post.getId();
    }

    public void edit(Long postId, Long userId, PostCreateRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BizException("帖子不存在"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BizException("用户不存在"));
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new BizException("板块不存在"));

        User author = post.getAuthor();
        if (user.getRole() != Role.ADMIN && !userId.equals(author.getId()))
            throw new ForbiddenException("无权操作");

        post.setTitle(request.title());
        post.setContent(request.content());
        post.setCategory(category);
        postRepository.save(post);
    }

    public void delete(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BizException("帖子不存在"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BizException("用户不存在"));

        User author = post.getAuthor();
        if (user.getRole() != Role.ADMIN && !userId.equals(author.getId()))
            throw new ForbiddenException("无权操作");

        postRepository.delete(post);
    }

    public boolean toggleLike(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BizException("帖子不存在"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BizException("用户不存在"));

        if (postLikeRepository.existsByUserAndPost(user, post)) {
            postLikeRepository.deleteByUserAndPost(user, post);
            return false;
        } else {
            PostLike like = new PostLike();
            like.setUser(user);
            like.setPost(post);
            postLikeRepository.save(like);
            return true;
        }
    }

    private PostSummaryInfo extractPostSummaryInfo(Post p) {
        String content = p.getContent();
        String trimmedContent = content.length() > 20
                ? content.substring(0, 20) + "..."
                : content;

        return new PostSummaryInfo(
                p.getId(),
                p.getTitle(),
                trimmedContent,
                p.getCategory().getName(),
                p.getAuthor().getUsername(),
                p.getViewCount(),
                postLikeRepository.countByPost(p),
                p.getCreatedAt()
        );
    }
}
