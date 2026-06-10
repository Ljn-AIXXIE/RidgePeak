package org.ridgepeak.backend.repositories;

import org.ridgepeak.backend.models.Post;
import org.ridgepeak.backend.models.PostLike;
import org.ridgepeak.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    boolean existsByUserAndPost(User user, Post post);
    void deleteByUserAndPost(User user, Post post);
    long countByPost(Post post);
}
