package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.dto.category.CategoryDtoOutput;
import ru.practicum.model.Category;
import ru.practicum.dto.category.CategoryDtoInput;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDtoOutput categoryToCategoryDtoOutput(Category category);

    Category categoryDtoInputToCategory(CategoryDtoInput categoryDtoInput);

    Category categoryDtoToCategory(CategoryDtoOutput categoryDtoOutput);
}
