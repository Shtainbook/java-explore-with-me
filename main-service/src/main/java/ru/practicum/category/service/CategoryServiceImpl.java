package ru.practicum.category.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.model.CategoryDto;
import ru.practicum.category.model.NewCategoryDto;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.util.pageable.OffsetPageRequest;
import ru.practicum.util.validation.SizeValidator;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, EventRepository eventRepository) {
        this.categoryRepository = categoryRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    @Transactional
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        log.info("Вызов метода createCategory с newCategoryDto={} ", newCategoryDto);
        return CategoryMapper.toCategoryDto(categoryRepository.save(CategoryMapper.toCategory(newCategoryDto)));
    }

    @Override
    public List<CategoryDto> getAllCategories(Integer from, Integer size) {
        SizeValidator.validateSize(size);
        log.info("Вызов метода getAllCategories с: from={}, size={}", from, size);
        return CategoryMapper.toCategoryDto(categoryRepository.findAll(OffsetPageRequest.of(from, size)).getContent());
    }

    @Override
    public CategoryDto getCategoryById(Long id) {
        log.info("Вызов метода getCategoryById с id={}", id);
        return CategoryMapper.toCategoryDto(getCategoryModelById(id));
    }

    @Override
    public Category getCategoryModelById(Long id) {
        log.info("Вызов метода getCategoryModelById с id={}", id);
        return categoryRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Category", id));
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long id, CategoryDto categoryDto) {
        Category element = getCategoryModelById(id);
        element.setName(categoryDto.getName());
        log.info("Вызов метода updateCategory с id={} и categoryDto={} ", id, categoryDto);
        return CategoryMapper.toCategoryDto(element);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        categoryExists(id);
        if (eventRepository.findFirstByCategoryId(id) != null) {
            throw new ConflictException("Значение существует.");
        }
        log.info("Вызов метода deleteCategory с id={}", id);
        categoryRepository.deleteById(id);
    }

    @Override
    public void categoryExists(Long id) {
        log.info("Вызов метода categoryExists с id={} ", id);
        if (!categoryRepository.existsById(id)) {
            throw new NotFoundException("Category", id);
        }
    }
}