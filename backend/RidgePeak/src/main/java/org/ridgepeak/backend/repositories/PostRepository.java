package org.ridgepeak.backend.repositories;

import org.ridgepeak.backend.models.Category;
import org.ridgepeak.backend.models.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByCategory(Category category, Pageable pageable);
    Page<Post> findByTitleContaining(String keyword, Pageable pageable);
    Page<Post> findByTitleContainingAndCategory(String keyword, Category category, Pageable pageable);
}
