package com.maurya.avenzo.service;

import com.maurya.avenzo.dto.request.CategoryRequestDto;
import com.maurya.avenzo.dto.response.CategoryResponseDto;
import com.maurya.avenzo.entity.CategoryEntity;
import com.maurya.avenzo.mapper.CategoryMapper;
import com.maurya.avenzo.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryResponseDto createCategory(CategoryRequestDto categoryRequestDto) {
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setName(categoryRequestDto.getName());

        CategoryEntity savedCategoryEntity = categoryRepository.save(categoryEntity);
        return categoryMapper.toCategoryDto(savedCategoryEntity);
    }

    public List<CategoryResponseDto> getAllCategories() {
        List<CategoryEntity> categories = categoryRepository.findAll();
        List<CategoryResponseDto> categoryResponseDtoList = new ArrayList<>();

        for (CategoryEntity category : categories) {
            CategoryResponseDto categoryResponseDto = categoryMapper.toCategoryDto(category);
            categoryResponseDtoList.add(categoryResponseDto);
        }

        return categoryResponseDtoList;
    }
}
