package ru.practicum.service.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.category.CategoryDtoInput;
import ru.practicum.dto.category.CategoryDtoOutput;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.exception.IncorrectDataException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.model.Category;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryDtoOutput addCategory(CategoryDtoInput categoryDtoInput) {
        if (categoryRepository.findByName(categoryDtoInput.getName()).isPresent()) {
            log.error("Категория с таким именем уже существует!");
            throw new IncorrectDataException("Внимание! Категория с таким именем уже существует!");
        }
        Category category = categoryMapper.categoryDtoInputToCategory(categoryDtoInput);
        categoryRepository.save(category);
        log.info("Информация о новой категории успешно сохранена!");
        return categoryMapper.categoryToCategoryDtoOutput(category);
    }

    @Override
    public CategoryDtoOutput updateCategory(CategoryDtoInput categoryDtoInput, long catId) {
        Category categoryFromDb = getCategoryFromDb(catId);
        if (categoryDtoInput.getName().equals(categoryFromDb.getName())) {
            log.info("Имя категории не изменилось.");
        } else {
            if (categoryRepository.findByName(categoryDtoInput.getName()).isPresent()) {
                log.error("Категория с таким именем уже существует!");
                throw new IncorrectDataException("Внимание! Категория с таким именем уже существует!");
            }
            categoryFromDb.setName(categoryDtoInput.getName());
        }
        categoryRepository.save(categoryFromDb);
        log.info("Информация о выбранной категории успешно обновлена!");
        return categoryMapper.categoryToCategoryDtoOutput(categoryFromDb);
    }

    @Override
    public void deleteCategory(long catId) {
        if (!categoryRepository.existsById(catId)) {
            log.error("Категории с таким уникальным номером не существует!");
            throw new EntityNotFoundException("Внимание! Категории с таким уникальным номером не существует!");
        } else {
            if (!eventRepository.findAllByCategoryId(catId).isEmpty()) {
                log.error("Нельзя удалить категорию, с привязанными к ней событиями!");
                throw new IncorrectDataException("Внимание! Нельзя удалить категорию, с привязанными к ней событиями!");
            }
            categoryRepository.deleteById(catId);
            log.info("Выбранная категория успешно удалена!");
        }
    }

    private Category getCategoryFromDb(long catId) {
        return categoryRepository.findById(catId).orElseThrow(() ->
                new EntityNotFoundException("Внимание! Категории с таким уникальным номером не существует!"));
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDtoOutput getCategory(long catId) {
        Category category = getCategoryFromDb(catId);
        log.info("Успешно получена информация о выбранной категории!");
        return categoryMapper.categoryToCategoryDtoOutput(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDtoOutput> getCategories(Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);
        return categoryRepository.findAll(pageable).getContent().stream()
                .map(categoryMapper::categoryToCategoryDtoOutput)
                .collect(Collectors.toList());
    }
}
