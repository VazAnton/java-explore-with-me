package ru.practicum.service.category;

import ru.practicum.model.dto.category.CategoryDto;
import ru.practicum.model.dto.category.NewCategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto addCategory(NewCategoryDto categoryDtoInput);

    CategoryDto updateCategory(NewCategoryDto categoryDtoInput, long catId);

    void deleteCategory(long catId);

    CategoryDto getCategory(long catId);

    List<CategoryDto> getCategories(Integer from, Integer size);
}
