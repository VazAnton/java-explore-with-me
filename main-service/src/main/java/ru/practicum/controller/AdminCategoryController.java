package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.category.CategoryDtoOutput;
import ru.practicum.dto.category.CategoryDtoInput;
import ru.practicum.service.category.CategoryService;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDtoOutput addCategory(@RequestBody @Valid CategoryDtoInput categoryDtoInput) {
        return categoryService.addCategory(categoryDtoInput);
    }

    @PatchMapping("/{catId}")
    public CategoryDtoOutput updateCategory(@RequestBody @Valid CategoryDtoInput categoryDtoInput, @PathVariable long catId) {
        return categoryService.updateCategory(categoryDtoInput, catId);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable long catId) {
        categoryService.deleteCategory(catId);
    }
}
