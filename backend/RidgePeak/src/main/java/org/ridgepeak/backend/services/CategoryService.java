package org.ridgepeak.backend.services;

import org.ridgepeak.backend.dtos.CategoryCreateRequest;
import org.ridgepeak.backend.exceptions.BizException;
import org.ridgepeak.backend.exceptions.ForbiddenException;
import org.ridgepeak.backend.models.Category;
import org.ridgepeak.backend.models.Role;
import org.ridgepeak.backend.models.User;
import org.ridgepeak.backend.repositories.CategoryRepository;
import org.ridgepeak.backend.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public CategoryService(CategoryRepository categoryRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category find(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BizException("板块不存在"));
    }

    public Category create(Long userId, CategoryCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BizException("用户不存在"));

        if (user.getRole() == Role.USER)
            throw new ForbiddenException("无权访问");
        if (categoryRepository.existsByName(request.name()))
            throw new BizException("该板块已存在");

        Category category = new Category();
        category.setName(request.name());
        category.setDescription(request.description());

        categoryRepository.save(category);

        return category;
    }

    public void delete(Long userId, Long categoryId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BizException("用户不存在"));
        if (user.getRole() == Role.USER)
            throw new ForbiddenException("无权访问");

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BizException("板块不存在"));
        categoryRepository.delete(category);
    }
}
