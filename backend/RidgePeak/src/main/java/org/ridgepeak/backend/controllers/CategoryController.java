package org.ridgepeak.backend.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.ridgepeak.backend.dtos.CategoryCreateRequest;
import org.ridgepeak.backend.models.Category;
import org.ridgepeak.backend.services.CategoryService;
import org.ridgepeak.backend.utils.Result;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/category")
public class CategoryController {
    public final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/list")
    public Result<?> list() {
        return Result.ok(categoryService.getAllCategories());
    }

    @PostMapping("/create")
    public Result<?> create(HttpServletRequest request, @RequestBody @Valid CategoryCreateRequest body) {
        Long userId = (Long) request.getAttribute("userId");
        Category category = categoryService.create(userId, body);
        return Result.ok(category);
    }

    @GetMapping("/{id}")
    public Result<?> get(@PathVariable Long categoryId) {
        Category category = categoryService.find(categoryId);
        return Result.ok(category);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(HttpServletRequest request, @PathVariable Long categoryId) {
        Long userId = (Long) request.getAttribute("userId");
        categoryService.delete(userId, categoryId);
        return Result.ok();
    }
}
