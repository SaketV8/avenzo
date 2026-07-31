package com.maurya.avenzo.mapper;

import com.maurya.avenzo.dto.request.CategoryRequestDto;
import com.maurya.avenzo.dto.response.CategoryResponseDto;
import com.maurya.avenzo.entity.CategoryEntity;
import jdk.jfr.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryResponseDto toCategoryDto(CategoryEntity category) {
        return new CategoryResponseDto(
                category.getId(),
                category.getName()
        );
    }
}
