package ru.practicum.ewm.category.common;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryMapper;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.error.exception.NotFoundException;
import ru.practicum.ewm.error.reasons_and_messages.ExceptionMessages;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PublicCategoryService {
    private final CategoryRepository categoryRepository;

    private final CategoryMapper categoryMapper;

    @Transactional(readOnly = true)
    public List<CategoryDto> getCategories(int from, int size) {
        List<Category> categories = categoryRepository.findAllOrderedById(from, size);
        return categoryMapper.toCategoryDto(categories);
    }

    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ExceptionMessages.CATEGORY_NOT_FOUND));

        return categoryMapper.toCategoryDto(category);
    }
}
