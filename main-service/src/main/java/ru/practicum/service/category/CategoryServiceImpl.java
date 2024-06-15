package ru.practicum.service.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.model.dto.category.CategoryDto;
import ru.practicum.model.dto.category.NewCategoryDto;
import ru.practicum.repository.CategoryRepository;

import java.util.List;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryDto addCategory(NewCategoryDto categoryDtoInput) {
        return null;
    }

    @Override
    public CategoryDto updateCategory(NewCategoryDto categoryDtoInput, long catId) {
        return null;
    }

    @Override
    public void deleteCategory(long catId) {

    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategory(long catId) {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        return null;
    }
}
