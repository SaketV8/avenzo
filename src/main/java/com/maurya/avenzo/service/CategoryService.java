package com.maurya.avenzo.service;

import com.maurya.avenzo.dto.request.CategoryRequestDto;
import com.maurya.avenzo.dto.response.CategoryResponseDto;
import com.maurya.avenzo.entity.CategoryEntity;
import com.maurya.avenzo.mapper.CategoryMapper;
import com.maurya.avenzo.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryResponseDto createCategory(CategoryRequestDto categoryRequestDto) {
        log.info("Started creating category. Name: {}", categoryRequestDto.getName());

        try {
            CategoryEntity categoryEntity = new CategoryEntity();
            categoryEntity.setName(categoryRequestDto.getName());
            CategoryEntity savedCategoryEntity = categoryRepository.save(categoryEntity);

            CategoryResponseDto response = categoryMapper.toCategoryDto(savedCategoryEntity);

            log.info("Successfully Created Category . CategoryId: {}, Name: {}", savedCategoryEntity.getId(), savedCategoryEntity.getName());

            return response;
        } catch (DataIntegrityViolationException ex) {
            log.error("Failed to create category. Name: {}", categoryRequestDto.getName(), ex);

            throw ex;
        } finally {
            log.info("Completed category creation request. Name: {}", categoryRequestDto.getName());
        }

    }

    public List<CategoryResponseDto> getAllCategories() {
        /*List<CategoryEntity> categories = categoryRepository.findAll();
        List<CategoryResponseDto> categoryResponseDtoList = new ArrayList<>();

        for (CategoryEntity category : categories) {
            CategoryResponseDto categoryResponseDto = categoryMapper.toCategoryDto(category);
            categoryResponseDtoList.add(categoryResponseDto);
        }

        return categoryResponseDtoList;*/

        log.info("Started fetching all categories");

        try {
            List<CategoryEntity> categories = categoryRepository.findAll();
            List<CategoryResponseDto> categoryResponseDtoList = new ArrayList<>();

            for (CategoryEntity category : categories) {
                CategoryResponseDto categoryResponseDto = categoryMapper.toCategoryDto(category);
                categoryResponseDtoList.add(categoryResponseDto);
            }

            log.info("Successfully fetched all categories. Count: {}", categoryResponseDtoList.size());

            return categoryResponseDtoList;

        } catch (Exception ex) {
            log.error("Failed to fetch all categories", ex);

            throw ex;
        } finally {
            log.info("Completed fetching all categories");
        }
    }
}
