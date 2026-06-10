package org.ridgepeak.backend.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.ridgepeak.backend.dtos.CategoryCreateRequest;
import org.ridgepeak.backend.dtos.CategoryInfo;
import org.ridgepeak.backend.services.CategoryService;
import org.ridgepeak.backend.utils.Result;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/category")
public class CategoryController {
    private final CategoryService categoryService;

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
        CategoryInfo categoryInfo = categoryService.create(userId, body);
        return Result.ok(categoryInfo);
    }

    @GetMapping("/{categoryId}")
    public Result<?> get(@PathVariable Long categoryId) {
        CategoryInfo categoryInfo = categoryService.find(categoryId);
        return Result.ok(categoryInfo);
    }

    @DeleteMapping("/{categoryId}")
    public Result<?> delete(HttpServletRequest request, @PathVariable Long categoryId) {
        Long userId = (Long) request.getAttribute("userId");
        categoryService.delete(userId, categoryId);
        return Result.ok();
    }
}
