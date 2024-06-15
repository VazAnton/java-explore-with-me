package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.model.category.Category;
import ru.practicum.model.dto.category.CategoryDto;
import ru.practicum.model.dto.category.NewCategoryDto;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDto categoryToCategoryDto(Category category);

    Category newCategoryDtoToCategory(NewCategoryDto categoryDtoInput);

    Category categoryDtoToCategory(CategoryDto categoryDto);
}
