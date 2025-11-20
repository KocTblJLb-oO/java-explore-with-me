package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.dto.NewCategoryDto;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.mapper.CategoryMapper;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.repository.EventRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final EventRepository eventRepository;

    public List<CategoryDto> getCategories(int from, int size) {
        log.info("getCategories. Получение категорий. Смещение: {}, количество: {}", from, size);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "id"));
        return categoryRepository.findAll(pageable).stream()
                .map(categoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    public CategoryDto getCategoryById(Long catId) {
        log.info("getCategory. Получение категории: {}", catId);
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new ValidationException("Категория id=" + catId + " не найдена",
                        HttpStatus.NOT_FOUND));
        return categoryMapper.toCategoryDto(category);
    }

    @Transactional
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        log.info("addCategory. Категория: {}", newCategoryDto);
        Category category = categoryMapper.toCategory(newCategoryDto);
        try {
            return categoryMapper.toCategoryDto(categoryRepository.save(category));
        } catch (DataIntegrityViolationException e) {
            throw new ValidationException("Имя категории " + newCategoryDto.getName() + " должно быть уникальным",
                    HttpStatus.CONFLICT);
        }
    }

    @Transactional
    public void deleteCategory(Long catId) {
        log.info("deleteCategory. Удаление категории с ИД: {}", catId);
        if (!categoryRepository.existsById(catId)) {
            throw new ValidationException("Категория id=" + catId + " не найдена", HttpStatus.NOT_FOUND);
        }

        if (eventRepository.existsByCategoryId(catId)) {
            throw new ValidationException("Нельзя удалить категорию с привязанными событиями", HttpStatus.CONFLICT);
        }

        categoryRepository.deleteById(catId);
    }

    @Transactional
    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        log.info("updateCategory. Новая категория: {}", categoryDto);
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new ValidationException("Категория id " + catId + " не найдена",
                        HttpStatus.NOT_FOUND));
        if (!category.getName().equals(categoryDto.getName()) &&
                categoryRepository.existsByName(categoryDto.getName())) {
            throw new ValidationException("Имя категории " + categoryDto.getName() + " должно быть уникальным",
                    HttpStatus.CONFLICT);
        }
        category.setName(categoryDto.getName());
        return categoryMapper.toCategoryDto(categoryRepository.save(category));
    }
}
