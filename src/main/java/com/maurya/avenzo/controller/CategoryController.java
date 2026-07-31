package com.maurya.avenzo.controller;

import com.maurya.avenzo.constant.UrlConstant;
import com.maurya.avenzo.dto.request.CategoryRequestDto;
import com.maurya.avenzo.dto.response.CategoryResponseDto;
import com.maurya.avenzo.service.CategoryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(UrlConstant.BASE_URL + "/category")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping()
    public ResponseEntity<CategoryResponseDto> createCategory(@Valid @RequestBody CategoryRequestDto categoryRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(categoryRequestDto));
    }

    @GetMapping()
    public ResponseEntity<List<CategoryResponseDto>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }
}
