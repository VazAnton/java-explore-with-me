package ru.practicum.service.category;

import ru.practicum.dto.category.CategoryDtoOutput;
import ru.practicum.dto.category.CategoryDtoInput;

import java.util.List;

public interface CategoryService {

    CategoryDtoOutput addCategory(CategoryDtoInput categoryDtoInput);

    CategoryDtoOutput updateCategory(CategoryDtoInput categoryDtoInput, long catId);

    void deleteCategory(long catId);

    CategoryDtoOutput getCategory(long catId);

    List<CategoryDtoOutput> getCategories(Integer from, Integer size);
}
