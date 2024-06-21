package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.category.CategoryDtoOutput;
import ru.practicum.service.category.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class PublicCategoryController {

    private final CategoryService categoryService;

    @GetMapping("/{catId}")
    public CategoryDtoOutput getCategory(@PathVariable long catId) {
        return categoryService.getCategory(catId);
    }

    @GetMapping
    public List<CategoryDtoOutput> getCategories(@RequestParam(defaultValue = "0") Integer from,
                                                 @RequestParam(defaultValue = "10") Integer size) {
        return categoryService.getCategories(from, size);
    }
}
