package ru.practicum.ewm.category.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryMapper;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.error.exception.ConditionsNotMetException;
import ru.practicum.ewm.error.exception.NotFoundException;
import ru.practicum.ewm.error.reasons_and_messages.ExceptionMessages;
import ru.practicum.ewm.event.EventRepository;

@Service
@RequiredArgsConstructor
public class AdminCategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    private final CategoryMapper categoryMapper;

    @Transactional
    public CategoryDto create(NewCategoryDto createDto) {
        Category category = categoryMapper.fromNewCategoryDto(createDto);
        Category saved = categoryRepository.save(category);
        return categoryMapper.toCategoryDto(saved);
    }

    @Transactional
    public void delete(long id) {
        if (!categoryRepository.existsById(id)) {
            throw new NotFoundException(ExceptionMessages.CATEGORY_NOT_FOUND);
        }
        if (eventRepository.existsByCategory_Id(id)) {
            throw new ConditionsNotMetException(ExceptionMessages.CATEGORY_NOT_EMPTY_CANNOT_BE_DELETED);
        }
        categoryRepository.deleteById(id);
    }

    @Transactional
    public CategoryDto update(long id, NewCategoryDto updateDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException(String.format(ExceptionMessages.CATEGORY_NOT_FOUND, id)));
        category.setName(updateDto.getName());
        return categoryMapper.toCategoryDto(category);
    }
}
